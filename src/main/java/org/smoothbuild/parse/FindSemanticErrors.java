package org.smoothbuild.parse;

import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.object.type.TypeNames.isGenericTypeName;
import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Strings.unescaped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.Named;
import org.smoothbuild.parse.ast.NamedNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.parse.ast.TypeNode;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableSet;

public class FindSemanticErrors {
  public static void findSemanticErrors(Definitions imported, Ast ast, Logger logger) {
    unescapeStrings(logger, ast);
    parametersReferenceWithParentheses(logger, ast);
    undefinedReferences(logger, imported, ast);
    undefinedTypes(logger, imported, ast);
    duplicateGlobalNames(logger, imported, ast);
    duplicateFieldNames(logger, ast);
    duplicateParamNames(logger, ast);
    defaultParamBeforeNonDefault(logger, ast);
    structNameStartingWithLowercaseLetter(logger, ast);
    firstFieldWithForbiddenType(logger, ast);
    functionResultTypeIsNotCoreTypeOfAnyParameter(logger, ast);
  }

  private static void unescapeStrings(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitString(StringNode string) {
        super.visitString(string);
        try {
          string.set(String.class, unescaped(string.value()));
        } catch (UnescapingFailedException e) {
          logger.log(parseError(string, e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void parametersReferenceWithParentheses(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        if (ref.hasParentheses()) {
          logger.log(parseError(ref, "Parameter '" + ref.name()
              + "' cannot be called as it is not a function."));
        }
      }
    }.visitAst(ast);
  }

  private static void undefinedReferences(Logger logger, Definitions imported, Ast ast) {
    Set<String> all = ImmutableSet.<String>builder()
        .addAll(imported.callables().keySet())
        .addAll(ast.callableNames())
        .build();
    new AstVisitor() {
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        if (!all.contains(call.name())) {
          logger.log(parseError(call.location(), "'" + call.name() + "' is undefined."));
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
        if (func.hasType()) {
          assertTypeIsDefined(func.typeNode());
        }
      }

      @Override
      public void visitParam(int index, ParamNode param) {
        assertTypeIsDefined(param.typeNode());
      }

      @Override
      public void visitField(int index, FieldNode field) {
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
    nameds.addAll(ast.funcs());
    nameds.sort(comparing(n -> n.location().line()));

    for (Named named : nameds) {
      logIfDuplicate(logger, imported.types(), named);
      logIfDuplicate(logger, imported.callables(), named);
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
      String atLocation = location.equals(Location.unknownLocation())
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
      public void visitFields(List<FieldNode> fields) {
        super.visitFields(fields);
        findDuplicateNames(logger, fields);
      }
    }.visitAst(ast);
  }

  private static void duplicateParamNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ParamNode> params) {
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
      public void visitParams(List<ParamNode> params) {
        super.visitParams(params);
        boolean foundParamWithDefaultValue = false;
        for (ParamNode param : params) {
          if (param.hasDefaultValue()) {
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

  private static void structNameStartingWithLowercaseLetter(Logger logger, Ast ast) {
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
        List<FieldNode> fields = struct.fields();
        if (!fields.isEmpty()) {
          FieldNode field = fields.get(0);
          TypeNode type = field.typeNode();
          if (type.isArray()) {
            logger.log(parseError(field, "First field of struct cannot have array type."));
          }
        }
        for (FieldNode field : fields) {
          if (isGenericTypeName(field.typeNode().name())) {
            logger.log(parseError(field, "Struct field cannot have a generic type.\n"));
          }
          if (field.typeNode().isNothing()) {
            logger.log(parseError(field, "Struct field cannot have 'Nothing' type."));
          }
        }
      }
    }.visitAst(ast);
  }

  private static void functionResultTypeIsNotCoreTypeOfAnyParameter(Logger logger,
      Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        if (func.hasType()
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
}
