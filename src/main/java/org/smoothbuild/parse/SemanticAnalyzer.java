package org.smoothbuild.parse;

import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.Sets.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FunctionNode;
import org.smoothbuild.util.Lists;

import com.google.common.collect.ImmutableSet;

public class SemanticAnalyzer {
  public static List<ParseError> findErrors(Functions functions, Ast ast) {
    List<ParseError> errors = duplicateFunctionErrors(functions, ast);
    errors.addAll(undefinedFunctionErrors(functions, ast));
    return errors;
  }

  private static List<ParseError> duplicateFunctionErrors(Functions functions,
      Ast ast) {
    Set<Name> defined = new HashSet<>();
    List<ParseError> errors = new ArrayList<>();
    for (FunctionNode node : ast.functions()) {
      Name name = node.name();
      if (defined.contains(name)) {
        errors.add(new ParseError(node.codeLocation(), "Function " + name
            + " is already defined."));
      }
      defined.add(name);
      if (functions.contains(name)) {
        errors.add(new ParseError(node.codeLocation(), "Function " + name
            + " cannot override builtin function with the same name."));
      }
    }
    return errors;
  }

  public static List<ParseError> undefinedFunctionErrors(Functions functions, Ast ast) {
    Map<Name, FunctionNode> functionNodes = ast.nameToFunctionMap();
    ImmutableSet<Name> all = ImmutableSet.<Name> builder()
        .addAll(functions.names())
        .addAll(functionNodes.keySet())
        .build();
    Set<Dependency> defined = map(all, name -> new Dependency(null, name));
    Set<Dependency> referenced = functionNodes
        .values()
        .stream()
        .map(FunctionNode::dependencies)
        .flatMap(fd -> fd.stream())
        .collect(toSet());
    referenced.removeAll(defined);
    return Lists.map(referenced, SemanticAnalyzer::unknownFunctionError);
  }

  private static ParseError unknownFunctionError(Dependency dependency) {
    return new ParseError(dependency.location(),
        "Call to unknown function " + dependency.functionName() + ".");
  }
}
