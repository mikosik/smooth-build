package org.smoothbuild.parse;

import static java.util.Collections.sort;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.lang.type.TypeNames.GENERIC;
import static org.smoothbuild.parse.ast.TypeNode.isGenericName;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.ast.ArgNode;
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
  public static List<ParseError> findSemanticErrors(SRuntime runtime, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    Functions functions = runtime.functions();
    unescapeStrings(errors, ast);
    parametersReferenceWithParentheses(errors, ast);
    undefinedReferences(errors, functions, ast);
    undefinedTypes(errors, runtime.types(), ast);
    duplicateGlobalNames(errors, runtime, ast);
    duplicateFieldNames(errors, ast);
    duplicateParamNames(errors, ast);
    duplicateArgNames(errors, ast);
    unknownArgNames(errors, functions, ast);
    structNameStartingWithLowercaseLetter(errors, ast);
    firstFieldWithForbiddenType(errors, ast);
    return errors;
  }

  private static void unescapeStrings(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitString(StringNode string) {
        super.visitString(string);
        try {
          string.set(String.class, unescaped(string.value()));
        } catch (UnescapingFailedException e) {
          errors.add(new ParseError(string, e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void parametersReferenceWithParentheses(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        if (ref.hasParentheses()) {
          errors.add(new ParseError(ref, "Parameter '" + ref.name()
              + "' cannot be called as it is not a function."));
        }
      }
    }.visitAst(ast);
  }

  private static void undefinedReferences(List<ParseError> errors, Functions functions, Ast ast) {
    Set<String> all = ImmutableSet.<String> builder()
        .addAll(functions.names())
        .addAll(map(ast.funcs(), f -> f.name()))
        .addAll(map(ast.structs(), s -> s.name()))
        .build();
    new AstVisitor() {
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        if (!all.contains(call.name())) {
          errors.add(new ParseError(call.location(), "'" + call.name() + "' is undefined."));
        }
      }
    }.visitAst(ast);
  }

  private static void undefinedTypes(List<ParseError> errors, RuntimeTypes types, Ast ast) {
    Set<String> all = ImmutableSet.<String> builder()
        .addAll(types.names())
        .addAll(map(ast.structs(), s -> s.name()))
        .build();
    new AstVisitor() {
      @Override
      public void visitType(TypeNode type) {
        super.visitType(type);
        assertTypeIsDefined(type);
      }

      private void assertTypeIsDefined(TypeNode type) {
        if (type instanceof ArrayTypeNode) {
          assertTypeIsDefined(((ArrayTypeNode) type).elementType());
        } else if (!all.contains(type.name())) {
          errors.add(new ParseError(type.location(), "Unknown type '" + type.name() + "'."));
        }
      }
    }.visitAst(ast);
  }

  private static void duplicateGlobalNames(List<ParseError> errors, SRuntime runtime, Ast ast) {
    Map<String, Object> defined = new HashMap<>();
    defined.putAll(runtime.functions().nameToFunctionMap());
    defined.putAll(runtime.types().nameToTypeMap());

    List<Named> nameds = new ArrayList<>();
    nameds.addAll(ast.structs());
    nameds.addAll(ast.funcs());
    sort(nameds, (node1, node2) -> {
      int l1 = node1.location().line();
      int l2 = node2.location().line();
      if (l1 < l2) {
        return -1;
      } else if (l1 == l2) {
        return 0;
      } else {
        return 1;
      }
    });
    for (Named named : nameds) {
      String name = named.name();
      if (defined.containsKey(name)) {
        Object otherDefinition = defined.get(name);
        String atLocation = (otherDefinition instanceof Named)
            ? " at " + ((Named) otherDefinition).location()
            : "";
        errors.add(new ParseError(named.location(),
            "'" + name + "' is already defined" + atLocation + "."));
      } else {
        defined.put(name, named);
      }
    }
  }

  private static void duplicateFieldNames(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitFields(List<FieldNode> fields) {
        super.visitFields(fields);
        findDuplicateNames(errors, fields);
      }
    }.visitAst(ast);
  }

  private static void duplicateParamNames(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitParams(List<ParamNode> params) {
        super.visitParams(params);
        findDuplicateNames(errors, params);
      }

    }.visitAst(ast);
  }

  private static void findDuplicateNames(List<ParseError> errors, List<? extends NamedNode> nodes) {
    Map<String, Location> alreadyDefined = new HashMap<>();
    for (NamedNode named : nodes) {
      String name = named.name();
      if (alreadyDefined.containsKey(name)) {
        errors.add(new ParseError(named, "'" + name + "' is already defined at "
            + alreadyDefined.get(name) + "."));
      }
      alreadyDefined.put(name, named.location());
    }
  }

  private static void duplicateArgNames(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitArgs(List<ArgNode> args) {
        super.visitArgs(args);
        Set<String> names = new HashSet<>();
        for (ArgNode arg : args) {
          if (arg.hasName()) {
            String name = arg.name();
            if (names.contains(name)) {
              errors.add(new ParseError(arg, "Argument '" + name + "' assigned twice."));
            }
            names.add(name);
          }
        }
      }
    }.visitAst(ast);
  }

  private static void unknownArgNames(List<ParseError> errors, Functions functions, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        Set<String> names = getParameters(call.name(), functions, ast);
        if (names != null) {
          for (ArgNode arg : call.args()) {
            if (arg.hasName() && !names.contains(arg.name())) {
              errors.add(new ParseError(arg, "Function '" + call.name()
                  + "' has no parameter '" + arg.name() + "'."));
            }
          }
        }
      }

      private Set<String> getParameters(String functionName, Functions functions, Ast ast) {
        if (ast.containsFunc(functionName)) {
          FuncNode funcNode = ast.func(functionName);
          return funcNode.params().stream()
              .map(p -> p.name())
              .collect(toSet());
        }
        if (functions.contains(functionName)) {
          return functions.get(functionName).parameters().stream()
              .map(p -> p.name())
              .collect(toSet());
        }
        return null;
      }
    }.visitAst(ast);
  }

  private static void structNameStartingWithLowercaseLetter(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        String name = struct.name();
        if (isGenericName(name)) {
          errors.add(new ParseError(struct.location(),
              "Struct name '" + name + "' should start with capital letter."));
        }
      }
    }.visitAst(ast);
  }

  private static void firstFieldWithForbiddenType(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        List<FieldNode> fields = struct.fields();
        if (!fields.isEmpty()) {
          FieldNode field = fields.get(0);
          TypeNode type = field.type();
          if (type instanceof ArrayTypeNode) {
            errors.add(new ParseError(field, "First field of struct cannot have array type."));
          }
        }
        for (FieldNode field : fields) {
          if (field.type().name().equals(GENERIC)) {
            errors.add(new ParseError(field, "Struct field cannot have a generic type.\n"));
          }
        }
      }
    }.visitAst(ast);
  }
}
