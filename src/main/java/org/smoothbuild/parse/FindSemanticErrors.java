package org.smoothbuild.parse;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.NamedNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.RefNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableSet;

public class FindSemanticErrors {
  public static List<ParseError> findSemanticErrors(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    unescapeStrings(errors, ast);
    parametersReferenceWithParentheses(errors, ast);
    undefinedElements(errors, functions, ast);
    duplicateFunctions(errors, functions, ast);
    duplicateParamNames(errors, ast);
    duplicateArgNames(errors, ast);
    unknownArgNames(errors, functions, ast);
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

  private static void undefinedElements(List<ParseError> errors, Functions functions, Ast ast) {
    Set<Name> all = ImmutableSet.<Name> builder()
        .addAll(functions.names())
        .addAll(map(ast.funcs(), f -> f.name()))
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

  private static void duplicateFunctions(List<ParseError> errors, Functions functions, Ast ast) {
    Map<Name, Location> defined = functions
        .nameToFunctionMap()
        .entrySet()
        .stream()
        .collect(toMap(e -> e.getKey(), e -> e.getValue().location()));
    new AstVisitor() {
      @Override
      public void visitFunc(FuncNode func) {
        super.visitFunc(func);
        Name name = func.name();
        if (defined.containsKey(name)) {
          errors.add(new ParseError(func, "Function '" + name
              + "' is already defined at " + defined.get(name) + "."));
        }
        defined.put(name, func.location());
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
    Map<Name, Location> alreadyDefined = new HashMap<>();
    for (NamedNode named : nodes) {
      Name name = named.name();
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
        Set<Name> names = new HashSet<>();
        for (ArgNode arg : args) {
          if (arg.hasName()) {
            Name name = arg.name();
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
        Set<Name> names = getParameters(call.name(), functions, ast);
        if (names != null) {
          for (ArgNode arg : call.args()) {
            if (arg.hasName() && !names.contains(arg.name())) {
              errors.add(new ParseError(arg, "Function '" + call.name()
                  + "' has no parameter '" + arg.name() + "'."));
            }
          }
        }
      }

      private Set<Name> getParameters(Name functionName, Functions functions, Ast ast) {
        if (ast.containsFuncs(functionName)) {
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
}
