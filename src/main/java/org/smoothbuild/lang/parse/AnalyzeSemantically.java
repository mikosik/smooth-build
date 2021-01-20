package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.base.type.Types.isVariableName;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.MemoryLogger;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.define.Scope;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.parse.ast.ArrayTypeNode;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.FunctionTypeNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.Named;
import org.smoothbuild.lang.parse.ast.NamedNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.StructNode.ConstructorNode;
import org.smoothbuild.lang.parse.ast.TypeNode;
import org.smoothbuild.lang.parse.ast.ValueNode;
import org.smoothbuild.util.DecodingHexException;
import org.smoothbuild.util.Sets;
import org.smoothbuild.util.UnescapingFailedException;

public class AnalyzeSemantically {
  public static List<Log> analyzeSemantically(Definitions imported, Ast ast) {
    var logger = new MemoryLogger();
    unescapeStringLiterals(logger, ast);
    decodeBlobLiterals(logger, ast);
    resolveReferences(logger, imported, ast);
    undefinedTypes(logger, imported, ast);
    duplicateGlobalNames(logger, imported, ast);
    duplicateFieldNames(logger, ast);
    duplicateParamNames(logger, ast);
    structNameWithSingleCapitalLetter(logger, ast);
    polytypeField(logger, ast);
    valueTypeIsPolytype(logger, ast);
    return logger.logs();
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
    var importedScope = new Scope<Named>(imported.referencables());
    Scope<Named> localScope = new Scope<>(importedScope, ast.referencablesMap());

    new AstVisitor() {
      Scope<Named> scope = localScope;
      @Override
      public void visitFunc(FuncNode func) {
        func.typeNode().ifPresent(this::visitType);

        var nameToParam = func.params()
            .stream()
            .collect(toImmutableMap(NamedNode::name, p -> p, (a, b) -> a));
        scope = new Scope<>(scope, nameToParam);
        func.expr().ifPresent(this::visitExpr);
        scope = scope.outerScope();

        visitCallable(func);
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        String name = ref.name();
        if (scope.contains(name)) {
          Named named = scope.get(name);
          if (named instanceof ReferencableNode referencable) {
            ref.setTarget(referencable);
          } else if (named instanceof Referencable referencable) {
            ref.setTarget(referencable);
          } else if (named instanceof ItemNode item) {
            ref.setTarget(item);
          } else {
            throw new RuntimeException("unexpected case: " + named.getClass().getCanonicalName());
          }
        } else {
          logger.log(parseError(ref.location(), "`" + name + "` is undefined."));
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
          logger.log(parseError(call.location(), "`" + name + "` is undefined."));
        }
      }
    }.visitAst(ast);
  }

  private static void undefinedTypes(Logger logger, Definitions imported, Ast ast) {
    Set<String> structNames = Sets.map(ast.structs(), NamedNode::name);
    new AstVisitor() {
      @Override
      public void visitConstructor(ConstructorNode constructor) {
        // intentionally empty to avoid calling visitParams() as synthetic constructor
        // should not be analyzed for semantic problems. Such problems are reported for
        // struct fields.
      }

      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        func.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        value.typeNode().ifPresent(this::assertTypeIsDefined);
      }

      @Override
      public void visitParam(int index, ItemNode param) {
        assertTypeIsDefined(param.typeNode());
      }

      @Override
      public void visitField(ItemNode field) {
        assertTypeIsDefined(field.typeNode());
      }

      private void assertTypeIsDefined(TypeNode type) {
        if (type instanceof ArrayTypeNode array) {
          assertTypeIsDefined(array.elementType());
        } else if (type instanceof FunctionTypeNode function) {
          assertTypeIsDefined(function.resultType());
          function.parameterTypes().forEach(this::assertTypeIsDefined);
        } else if (!isDefinedType(type)) {
          logger.log(parseError(type.location(), "Undefined type " + type.q() + "."));
        }
      }

      private boolean isDefinedType(TypeNode type) {
        return isVariableName(type.name())
            || structNames.contains(type.name())
            || imported.types().containsKey(type.name());
      }
    }.visitAst(ast);
  }

  private static void duplicateGlobalNames(Logger logger, Definitions imported, Ast ast) {
    List<Named> nameds = new ArrayList<>();
    nameds.addAll(ast.structs());
    nameds.addAll(map(ast.structs(), StructNode::constructor));
    nameds.addAll(ast.referencable());
    nameds.sort(comparing(n -> n.location().line()));

    for (Named named : nameds) {
      logIfDuplicate(logger, imported.types(), named);
      logIfDuplicate(logger, imported.referencables(), named);
    }
    Map<String, Named> checked = new HashMap<>();
    for (Named named : nameds) {
      logIfDuplicate(logger, checked, named);
      checked.put(named.name(), named);
    }
  }

  private static void logIfDuplicate(
      Logger logger, Map<String, ? extends Named> others, Named named) {
    String name = named.name();
    if (others.containsKey(name)) {
      Named otherDefinition = others.get(name);
      Location location = otherDefinition.location();
      logger.log(alreadyDefinedError(named, location));
    }
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

      @Override
      public void visitConstructor(ConstructorNode constructor) {
        // intentionally empty to avoid calling visitParams() as synthetic constructor
        // should not be analyzed for semantic problems. Such problems are reported for
        // struct fields.
      }
    }.visitAst(ast);
  }

  private static void findDuplicateNames(Logger logger, List<? extends NamedNode> nodes) {
    Map<String, Location> alreadyDefined = new HashMap<>();
    for (NamedNode named : nodes) {
      String name = named.name();
      if (alreadyDefined.containsKey(name)) {
        logger.log(alreadyDefinedError(named, alreadyDefined.get(name)));
      }
      alreadyDefined.put(name, named.location());
    }
  }

  private static void structNameWithSingleCapitalLetter(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        String name = struct.name();
        if (isVariableName(name)) {
          logger.log(parseError(struct.location(),
              "`" + name + "` is illegal struct name. It must have at least two characters."));
        }
      }
    }.visitAst(ast);
  }

  private static void polytypeField(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        List<ItemNode> fields = struct.fields();
        for (ItemNode field : fields) {
          if (field.typeNode().isPolytype()) {
            logger.log(parseError(field, "Struct field type cannot have type variable."));
          }
        }
      }
    }.visitAst(ast);
  }

  private static void valueTypeIsPolytype(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitValue(ValueNode value) {
        super.visitValue(value);
        value.typeNode().ifPresent(typeNode -> {
          if (typeNode.isPolytype()) {
            logger.log(parseError(typeNode, "Value type cannot have type variables."));
          }
        });
      }
    }.visitAst(ast);
  }

  private static Log alreadyDefinedError(Named named, Location location) {
    String atLocation = location.equals(Location.internal())
        ? ""
        : " at " + location;
    return parseError(
        named.location(), "`" + named.name() + "` is already defined" + atLocation + ".");
  }
}
