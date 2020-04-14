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

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
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
  public static List<String> findSemanticErrors(SRuntime runtime, Ast ast) {
    List<String> errors = new ArrayList<>();
    Functions functions = runtime.functions();
    unescapeStrings(errors, ast);
    parametersReferenceWithParentheses(errors, ast);
    undefinedReferences(errors, functions, ast);
    undefinedTypes(errors, runtime.objectFactory(), ast);
    duplicateGlobalNames(errors, runtime, ast);
    duplicateFieldNames(errors, ast);
    duplicateParamNames(errors, ast);
    defaultParamBeforeNonDefault(errors, ast);
    structNameStartingWithLowercaseLetter(errors, ast);
    firstFieldWithForbiddenType(errors, ast);
    functionResultTypeIsNotCoreTypeOfAnyParameter(errors, ast);
    return errors;
  }

  private static void unescapeStrings(List<String> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitString(StringNode string) {
        super.visitString(string);
        try {
          string.set(String.class, unescaped(string.value()));
        } catch (UnescapingFailedException e) {
          errors.add(parseError(string, e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void parametersReferenceWithParentheses(List<String> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        if (ref.hasParentheses()) {
          errors.add(parseError(ref, "Parameter '" + ref.name()
              + "' cannot be called as it is not a function."));
        }
      }
    }.visitAst(ast);
  }

  private static void undefinedReferences(List<String> errors, Functions functions, Ast ast) {
    Set<String> all = ImmutableSet.<String>builder()
        .addAll(functions.names())
        .addAll(map(ast.funcs(), NamedNode::name))
        .addAll(map(ast.structs(), structNode -> structNode.constructor().name()))
        .build();
    new AstVisitor() {
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        if (!all.contains(call.name())) {
          errors.add(parseError(call.location(), "'" + call.name() + "' is undefined."));
        }
      }
    }.visitAst(ast);
  }

  private static void undefinedTypes(List<String> errors, ObjectFactory objectFactory, Ast ast) {
    List<String> structNames = map(ast.structs(), NamedNode::name);
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        if (func.hasType()) {
          assertTypeIsDefined(func.type());
        }
      }

      @Override
      public void visitParam(int index, ParamNode param) {
        assertTypeIsDefined(param.type());
      }

      @Override
      public void visitField(int index, FieldNode field) {
        assertTypeIsDefined(field.type());
      }

      private void assertTypeIsDefined(TypeNode type) {
        if (type.isArray()) {
          assertTypeIsDefined(((ArrayTypeNode) type).elementType());
        } else if (!isDefinedType(type)) {
          errors.add(parseError(type.location(), "Undefined type '" + type.name() + "'."));
        }
      }

      private boolean isDefinedType(TypeNode type) {
        return isGenericTypeName(type.name())
            || structNames.contains(type.name())
            || objectFactory.containsType(type.name());
      }
    }.visitAst(ast);
  }

  private static void duplicateGlobalNames(List<String> errors, SRuntime runtime, Ast ast) {
    Functions functions = runtime.functions();
    ObjectFactory objectFactory = runtime.objectFactory();
    Map<String, Named> defined = new HashMap<>(functions.nameToFunctionMap());
    List<Named> nameds = new ArrayList<>();
    nameds.addAll(ast.structs());
    nameds.addAll(map(ast.structs(), StructNode::constructor));
    nameds.addAll(ast.funcs());
    nameds.sort(comparing(n -> n.location().line()));
    for (Named named : nameds) {
      String name = named.name();
      if (defined.containsKey(name)) {
        Named otherDefinition = defined.get(name);
        String atLocation = " at " + otherDefinition.location();
        errors.add(alreadyDefinedError(named, name, atLocation));
      } else {
        if (objectFactory.containsType(name)) {
          errors.add(alreadyDefinedError(named, name, ""));
        }  else {
          defined.put(name, named);
        }
      }
    }
  }

  private static String alreadyDefinedError(Named named, String name, String atLocation) {
    return parseError(named.location(), "'" + name + "' is already defined" + atLocation + ".");
  }

  private static void duplicateFieldNames(List<String> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFields(List<FieldNode> fields) {
        super.visitFields(fields);
        findDuplicateNames(errors, fields);
      }
    }.visitAst(ast);
  }

  private static void duplicateParamNames(List<String> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ParamNode> params) {
        super.visitParams(params);
        findDuplicateNames(errors, params);
      }
    }.visitAst(ast);
  }

  private static void findDuplicateNames(List<String> errors, List<? extends NamedNode> nodes) {
    Map<String, Location> alreadyDefined = new HashMap<>();
    for (NamedNode named : nodes) {
      String name = named.name();
      if (alreadyDefined.containsKey(name)) {
        errors.add(parseError(named, "'" + name + "' is already defined at "
            + alreadyDefined.get(name) + "."));
      }
      alreadyDefined.put(name, named.location());
    }
  }

  private static void defaultParamBeforeNonDefault(List<String> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ParamNode> params) {
        super.visitParams(params);
        boolean foundParamWithDefaultValue = false;
        for (ParamNode param : params) {
          if (param.hasDefaultValue()) {
            foundParamWithDefaultValue = true;
          } else if (foundParamWithDefaultValue) {
            errors.add(parseError(param,
                "parameter with default value must be placed after all parameters " +
                    "which don't have default value.\n"));
          }
        }
      }
    }.visitAst(ast);
  }

  private static void structNameStartingWithLowercaseLetter(List<String> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        String name = struct.name();
        if (isGenericTypeName(name)) {
          errors.add(parseError(struct.location(),
              "'" + name + "' is illegal struct name. It must have at least two characters."));
        }
      }
    }.visitAst(ast);
  }

  private static void firstFieldWithForbiddenType(List<String> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        List<FieldNode> fields = struct.fields();
        if (!fields.isEmpty()) {
          FieldNode field = fields.get(0);
          TypeNode type = field.type();
          if (type.isArray()) {
            errors.add(parseError(field, "First field of struct cannot have array type."));
          }
        }
        for (FieldNode field : fields) {
          if (isGenericTypeName(field.type().name())) {
            errors.add(parseError(field, "Struct field cannot have a generic type.\n"));
          }
          if (field.type().isNothing()) {
            errors.add(parseError(field, "Struct field cannot have 'Nothing' type."));
          }
        }
      }
    }.visitAst(ast);
  }

  private static void functionResultTypeIsNotCoreTypeOfAnyParameter(List<String> errors,
      Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        if (func.hasType()
            && func.type().isGeneric()
            && !hasParamWithCoreTypeEqualToResultCoreType(func)) {
          errors.add(parseError(func.type(), "Undefined generic type '"
              + func.type().coreType().name()
              + "'. Only generic types used in declaration of function parameters "
              + "can be used here."));
        }
      }

      private boolean hasParamWithCoreTypeEqualToResultCoreType(FuncNode func) {
        String name = func.type().coreType().name();
        return func.params()
            .stream()
            .anyMatch(p -> p.type().coreType().name().equals(name));
      }
    }.visitAst(ast);
  }
}
