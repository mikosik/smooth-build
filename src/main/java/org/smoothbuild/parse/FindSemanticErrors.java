package org.smoothbuild.parse;

import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.TypeNode;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableSet;

public class FindSemanticErrors {
  public static List<ParseError> findSemanticErrors(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    unescapeStrings(errors, ast);
    overridenBuiltinFunctions(errors, functions, ast);
    duplicateFunctions(errors, functions, ast);
    undefinedFunctions(errors, functions, ast);
    duplicateParamNames(errors, ast);
    duplicateArgNames(errors, ast);
    unknownArgNames(errors, functions, ast);
    nestedArrayTypeParams(errors, ast);
    return errors;
  }

  private static void unescapeStrings(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      public void visitString(StringNode string) {
        super.visitString(string);
        try {
          string.set(String.class, unescaped(string.value()));
        } catch (UnescapingFailedException e) {
          errors.add(new ParseError(string.codeLocation(), e.getMessage()));
        }
      }
    }.visitAst(ast);
  }

  private static void overridenBuiltinFunctions(List<ParseError> errors, Functions functions,
      Ast ast) {
    new AstVisitor() {
      public void visitFunction(FuncNode function) {
        super.visitFunction(function);
        if (functions.contains(function.name())) {
          errors.add(new ParseError(function.codeLocation(), "Function " + function.name()
              + " cannot override builtin function with the same name."));
        }
      }
    }.visitAst(ast);
  }

  private static void duplicateFunctions(List<ParseError> errors, Functions functions, Ast ast) {
    Set<Name> defined = new HashSet<>();
    new AstVisitor() {
      public void visitFunction(FuncNode function) {
        super.visitFunction(function);
        if (defined.contains(function.name())) {
          errors.add(new ParseError(function.codeLocation(), "Function " + function.name()
              + " is already defined."));
        }
        defined.add(function.name());
      }
    }.visitAst(ast);
  }

  private static void undefinedFunctions(List<ParseError> errors, Functions functions, Ast ast) {
    Set<Name> all = ImmutableSet.<Name> builder()
        .addAll(functions.names())
        .addAll(map(ast.functions(), f -> f.name()))
        .build();
    new AstVisitor() {
      public void visitCall(CallNode call) {
        super.visitCall(call);
        if (!all.contains(call.name())) {
          errors.add(new ParseError(call.codeLocation(),
              "Call to unknown function " + call.name() + "."));
        }
      }
    }.visitAst(ast);
  }

  private static void duplicateParamNames(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      public void visitParams(List<ParamNode> params) {
        super.visitParams(params);
        Set<Name> names = new HashSet<>();
        for (ParamNode param : params) {
          Name name = param.name();
          if (names.contains(name)) {
            errors.add(new ParseError(param.codeLocation(), "Duplicate parameter " + name + "."));
          }
          names.add(name);
        }
      }
    }.visitAst(ast);
  }

  private static void nestedArrayTypeParams(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      public void visitParams(List<ParamNode> params) {
        super.visitParams(params);
        for (ParamNode node : params) {
          TypeNode type = node.type();
          if (type instanceof ArrayTypeNode
              && ((ArrayTypeNode) type).elementType() instanceof ArrayTypeNode) {
            errors.add(new ParseError(node.codeLocation(), "Nested array type is forbidden."));
          }
        }
      }
    }.visitAst(ast);
  }

  private static void duplicateArgNames(List<ParseError> errors, Ast ast) {
    new AstVisitor() {
      public void visitArgs(List<ArgNode> args) {
        super.visitArgs(args);
        Set<Name> names = new HashSet<>();
        for (ArgNode arg : args) {
          if (arg.hasName()) {
            Name name = arg.name();
            if (names.contains(name)) {
              errors.add(new ParseError(arg.codeLocation(), "Argument " + name
                  + " assigned twice."));
            }
            names.add(name);
          }
        }
      }
    }.visitAst(ast);
  }

  private static void unknownArgNames(List<ParseError> errors, Functions functions, Ast ast) {
    new AstVisitor() {
      public void visitCall(CallNode call) {
        super.visitCall(call);
        Set<Name> names = getParameters(call.name(), functions, ast);
        if (names != null) {
          for (ArgNode arg : call.args()) {
            if (arg.hasName() && !names.contains(arg.name())) {
              errors.add(new ParseError(arg.codeLocation(), "Function " + call.name()
                  + " has no parameter '" + arg.name().value() + "'."));
            }
          }
        }
      }

      private Set<Name> getParameters(Name functionName, Functions functions, Ast ast) {
        FuncNode funcNode = ast.nameToFunctionMap().get(functionName);
        if (funcNode != null) {
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
}
