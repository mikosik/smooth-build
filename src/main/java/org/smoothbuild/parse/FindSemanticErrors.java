package org.smoothbuild.parse;

import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstWalker;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.TypeNode;

import com.google.common.collect.ImmutableSet;

public class FindSemanticErrors {
  public static List<ParseError> findSemanticErrors(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    errors.addAll(overridenBuiltinFunctions(functions, ast));
    errors.addAll(duplicateFunctions(functions, ast));
    errors.addAll(undefinedFunctions(functions, ast));
    errors.addAll(duplicateParamNames(ast));
    errors.addAll(unknownParamTypes(ast));
    errors.addAll(nestedArrayTypeParams(ast));
    return errors;
  }

  private static List<ParseError> overridenBuiltinFunctions(Functions functions, Ast ast) {
    return new ErrorAstWalker() {
      public List<ParseError> visitFunction(FuncNode function) {
        List<ParseError> errors = super.visitFunction(function);
        if (functions.contains(function.name())) {
          errors.add(new ParseError(function.codeLocation(), "Function " + function.name()
              + " cannot override builtin function with the same name."));
        }
        return errors;
      }
    }.visitAst(ast);
  }

  private static List<ParseError> duplicateFunctions(Functions functions, Ast ast) {
    Set<Name> defined = new HashSet<>();
    return new ErrorAstWalker() {
      public List<ParseError> visitFunction(FuncNode function) {
        List<ParseError> errors = super.visitFunction(function);
        if (defined.contains(function.name())) {
          errors.add(new ParseError(function.codeLocation(), "Function " + function.name()
              + " is already defined."));
        }
        defined.add(function.name());
        return errors;
      }
    }.visitAst(ast);
  }

  private static List<ParseError> undefinedFunctions(Functions functions, Ast ast) {
    Set<Name> all = ImmutableSet.<Name> builder()
        .addAll(functions.names())
        .addAll(map(ast.functions(), f -> f.name()))
        .build();

    return new ErrorAstWalker() {
      public List<ParseError> visitCall(CallNode call) {
        List<ParseError> errors = super.visitCall(call);
        if (!all.contains(call.name())) {
          errors.add(new ParseError(call.codeLocation(),
              "Call to unknown function " + call.name() + "."));
        }
        return errors;
      }
    }.visitAst(ast);
  }

  private static List<ParseError> duplicateParamNames(Ast ast) {
    return new ErrorAstWalker() {
      public List<ParseError> visitParams(List<ParamNode> params) {
        List<ParseError> errors = super.visitParams(params);
        Set<String> names = new HashSet<>();
        for (ParamNode param : params) {
          String name = param.name();
          if (names.contains(name)) {
            errors.add(new ParseError(param.codeLocation(), "Duplicate parameter '" + name + "'."));
          }
          names.add(name);
        }
        return errors;
      }
    }.visitAst(ast);
  }

  private static List<ParseError> unknownParamTypes(Ast ast) {
    return new ErrorAstWalker() {
      public List<ParseError> visitParams(List<ParamNode> params) {
        List<ParseError> errors = super.visitParams(params);
        for (ParamNode node : params) {
          TypeNode type = node.typeNode();
          while (type instanceof ArrayTypeNode) {
            type = ((ArrayTypeNode) type).elementType();
          }
          if (Types.basicTypeFromString(type.name()) == null) {
            errors.add(new ParseError(type.codeLocation(), "Unknown type '" + type.name() + "'."));
          }
        }
        return errors;
      }
    }.visitAst(ast);
  }

  private static List<ParseError> nestedArrayTypeParams(Ast ast) {
    return new ErrorAstWalker() {
      public List<ParseError> visitParams(List<ParamNode> params) {
        List<ParseError> errors = super.visitParams(params);
        for (ParamNode node : params) {
          TypeNode type = node.typeNode();
          if (type instanceof ArrayTypeNode
              && ((ArrayTypeNode) type).elementType() instanceof ArrayTypeNode) {
            errors.add(new ParseError(node.codeLocation(), "Nested array type is forbidden."));
          }
        }
        return errors;
      }
    }.visitAst(ast);
  }

  private static class ErrorAstWalker extends AstWalker<List<ParseError>> {
    @Override
    public List<ParseError> reduce(List<ParseError> a, List<ParseError> b) {
      ArrayList<ParseError> result = new ArrayList<>();
      result.addAll(a);
      result.addAll(b);
      return result;
    }

    @Override
    public List<ParseError> reduceIdentity() {
      return new ArrayList<>();
    }
  }
}
