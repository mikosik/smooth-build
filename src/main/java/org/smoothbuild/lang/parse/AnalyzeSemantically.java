package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.base.type.Types.isGenericTypeName;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.parse.ast.ArrayTypeNode;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.CallableNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.Named;
import org.smoothbuild.lang.parse.ast.NamedNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.TypeNode;
import org.smoothbuild.lang.parse.ast.ValueNode;
import org.smoothbuild.lang.parse.ast.ValueTarget;
import org.smoothbuild.util.DecodingHexException;
import org.smoothbuild.util.UnescapingFailedException;

public class AnalyzeSemantically {
  public static void analyzeSemantically(Definitions imported, Ast ast, Logger logger) {
    unescapeStringLiterals(logger, ast);
    decodeBlobLiterals(logger, ast);
    resolveReferences(logger, imported, ast);
    undefinedTypes(logger, imported, ast);
    duplicateGlobalNames(logger, imported, ast);
    duplicateFieldNames(logger, ast);
    duplicateParamNames(logger, ast);
    defaultParamBeforeNonDefault(logger, ast);
    structNameWithSingleCapitalLetter(logger, ast);
    firstFieldWithForbiddenType(logger, ast);
    functionResultTypeIsNotCoreTypeOfAnyParameter(logger, ast);
    valueTypeIsGeneric(logger, ast);
  }

  private static void unescapeStringLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStringLiteral(StringNode string) {
        super.visitStringLiteral(string);
        try {
          string.calculateUnescaped();
        } catch (UnescapingFailedException e) {
          logger.log(parseError(string, e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void decodeBlobLiterals(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitBlobLiteral(BlobNode blob) {
        super.visitBlobLiteral(blob);
        try {
          blob.decodeByteString();
        } catch (DecodingHexException e) {
          logger.log(parseError(blob, "Illegal Blob literal. " + e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void resolveReferences(Logger logger, Definitions imported, Ast ast) {
    var importedScope = new Scope<Named>(imported.evaluables());
    Scope<Named> localScope = new Scope<>(importedScope, ast.evaluablesMap());

    new AstVisitor() {
      Scope<Named> scope = localScope;
      @Override
      public void visitFunc(FuncNode func) {
        func.visitType(this);

        var nameToParam = func.params()
            .stream()
            .collect(toImmutableMap(NamedNode::name, p -> p, (a, b) -> a));
        scope = new Scope<>(scope, nameToParam);
        func.visitExpr(this);
        scope = scope.outerScope();

        visitCallable(func);
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        String name = ref.name();
        if (scope.contains(name)) {
          Named named = scope.get(name);
          if (named instanceof ValueNode value) {
            ref.setTarget(value);
          } else if (named instanceof Value value) {
            ref.setTarget(new ValueTarget(value));
          } else if (named instanceof ItemNode item) {
            ref.setTarget(item);
          } else if (named instanceof CallableNode || named instanceof Callable) {
            logger.log(parseError(ref.location(), "'" + name + "' is a function and cannot be " +
                "accessed as a value."));
          } else {
            throw new RuntimeException("unexpected case: " + named.getClass().getCanonicalName());
          }
        } else {
          logger.log(parseError(ref.location(), "'" + name + "' is undefined."));
          logger.log(parseError(ref.location(), "Available names: " + scope.namesToString()));
        }
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        String name = call.calledName();
        if (scope.contains(name)) {
          Named named = scope.get(name);
          if (named instanceof ItemNode) {
            logger.log(parseError(call.location(), "Parameter `" + name
                + "` cannot be called as it is not a function."));
          } else if (named instanceof ValueNode || named instanceof Value) {
            logger.log(parseError(call.location(), "`" + name
                + "` cannot be called as it is a value."));
          }
        } else {
          logger.log(parseError(call.location(), "'" + name + "' is undefined."));
          logger.log(parseError(call.location(), "Available names: " + scope.namesToString()));
        }
      }
    }.visitAst(ast);
  }

  private static void undefinedTypes(Logger logger, Definitions imported, Ast ast) {
    List<String> structNames = map(ast.structs(), NamedNode::name);
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        if (func.declaresType()) {
          assertTypeIsDefined(func.typeNode());
        }
      }

      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        if (value.declaresType()) {
          assertTypeIsDefined(value.typeNode());
        }
      }

      @Override
      public void visitParam(int index, ItemNode param) {
        assertTypeIsDefined(param.typeNode());
      }

      @Override
      public void visitField(int index, ItemNode field) {
        assertTypeIsDefined(field.typeNode());
      }

      private void assertTypeIsDefined(TypeNode type) {
        if (type.isArray()) {
          assertTypeIsDefined(((ArrayTypeNode) type).elementType());
        } else if (!isDefinedType(type)) {
          logger.log(parseError(type.location(), "Undefined type '" + type.name() + "'."));
        }
      }

      private boolean isDefinedType(TypeNode type) {
        return isGenericTypeName(type.name())
            || structNames.contains(type.name())
            || imported.types().containsKey(type.name());
      }
    }.visitAst(ast);
  }

  private static void duplicateGlobalNames(Logger logger, Definitions imported, Ast ast) {
    List<Named> nameds = new ArrayList<>();
    nameds.addAll(ast.structs());
    nameds.addAll(map(ast.structs(), StructNode::constructor));
    nameds.addAll(ast.evaluables());
    nameds.sort(comparing(n -> n.location().line()));

    for (Named named : nameds) {
      logIfDuplicate(logger, imported.types(), named);
      logIfDuplicate(logger, imported.evaluables(), named);
    }
    Map<String, Named> checked = new HashMap<>();
    for (Named named : nameds) {
      logIfDuplicate(logger, checked, named);
      checked.put(named.name(), named);
    }
  }

  private static void logIfDuplicate(
      Logger logger, Map<String, ? extends Named> types, Named named) {
    String name = named.name();
    if (types.containsKey(name)) {
      Named otherDefinition = types.get(name);
      Location location = otherDefinition.location();
      String atLocation = location.equals(Location.internal())
          ? ""
          : " at " + location;
      logger.log(alreadyDefinedError(named, name, atLocation));
    }
  }

  private static Log alreadyDefinedError(Named named, String name, String atLocation) {
    return parseError(named.location(), "'" + name + "' is already defined" + atLocation + ".");
  }

  private static void duplicateFieldNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFields(List<ItemNode> fields) {
        super.visitFields(fields);
        findDuplicateNames(logger, fields);
      }
    }.visitAst(ast);
  }

  private static void duplicateParamNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ItemNode> params) {
        super.visitParams(params);
        findDuplicateNames(logger, params);
      }
    }.visitAst(ast);
  }

  private static void findDuplicateNames(Logger logger, List<? extends NamedNode> nodes) {
    Map<String, Location> alreadyDefined = new HashMap<>();
    for (NamedNode named : nodes) {
      String name = named.name();
      if (alreadyDefined.containsKey(name)) {
        logger.log(parseError(named, "'" + name + "' is already defined at "
            + alreadyDefined.get(name) + "."));
      }
      alreadyDefined.put(name, named.location());
    }
  }

  private static void defaultParamBeforeNonDefault(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ItemNode> params) {
        super.visitParams(params);
        boolean foundParamWithDefaultValue = false;
        for (ItemNode param : params) {
          if (param.defaultValue().isPresent()) {
            foundParamWithDefaultValue = true;
          } else if (foundParamWithDefaultValue) {
            logger.log(parseError(param,
                "parameter with default value must be placed after all parameters " +
                    "which don't have default value.\n"));
          }
        }
      }
    }.visitAst(ast);
  }

  private static void structNameWithSingleCapitalLetter(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        String name = struct.name();
        if (isGenericTypeName(name)) {
          logger.log(parseError(struct.location(),
              "'" + name + "' is illegal struct name. It must have at least two characters."));
        }
      }
    }.visitAst(ast);
  }

  private static void firstFieldWithForbiddenType(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        List<ItemNode> fields = struct.fields();
        if (!fields.isEmpty()) {
          ItemNode field = fields.get(0);
          TypeNode type = field.typeNode();
          if (type.isArray()) {
            logger.log(parseError(field, "First field of struct cannot have array type."));
          }
          if (type.isNothing()) {
            logger.log(parseError(field, "First field of struct cannot have 'Nothing' type."));
          }
        }
        for (ItemNode field : fields) {
          if (isGenericTypeName(field.typeNode().name())) {
            logger.log(parseError(field, "Struct field cannot have a generic type.\n"));
          }
        }
      }
    }.visitAst(ast);
  }

  private static void functionResultTypeIsNotCoreTypeOfAnyParameter(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        if (func.declaresType()
            && func.typeNode().isGeneric()
            && !hasParamWithCoreTypeEqualToResultCoreType(func)) {
          logger.log(parseError(func.typeNode(), "Undefined generic type '"
              + func.typeNode().coreType().name()
              + "'. Only generic types used in declaration of function parameters "
              + "can be used here."));
        }
      }

      private boolean hasParamWithCoreTypeEqualToResultCoreType(FuncNode func) {
        String name = func.typeNode().coreType().name();
        return func.params()
            .stream()
            .anyMatch(p -> p.typeNode().coreType().name().equals(name));
      }
    }.visitAst(ast);
  }

  private static void valueTypeIsGeneric(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        if (value.declaresType() && value.typeNode().isGeneric()) {
          logger.log(parseError(value.typeNode(), "Value cannot have generic type."));
        }
      }
    }.visitAst(ast);
  }
}
