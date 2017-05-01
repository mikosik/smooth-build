package org.smoothbuild.parse;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.parse.Maybe.errors;
import static org.smoothbuild.parse.Maybe.value;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableSet;

/**
 * Collects functions' dependencies (names of functions that are called within
 * given function).
 */
public class DependencyCollector {
  public static Maybe<Map<Name, FunctionNode>> collectDependencies(
      Map<Name, FunctionNode> functionNodes, Functions functions) {
    return detectUndefinedFunctions(functions, functionNodes);
  }

  public static Maybe<Map<Name, FunctionNode>> detectUndefinedFunctions(Functions functions,
      Map<Name, FunctionNode> functionNodes) {
    Set<Dependency> defined = ImmutableSet.<Name> builder()
        .addAll(functions.names())
        .addAll(functionNodes.keySet())
        .build()
        .stream()
        .map(name -> new Dependency(null, name))
        .collect(toSet());
    Set<Dependency> referenced = functionNodes
        .values()
        .stream()
        .map(FunctionNode::dependencies)
        .flatMap(fd -> fd.stream())
        .collect(toSet());
    referenced.removeAll(defined);
    if (referenced.isEmpty()) {
      return value(functionNodes);
    } else {
      List<ParseError> errors = referenced
          .stream()
          .map(DependencyCollector::unknownFunctionError)
          .collect(toList());
      return errors(errors);
    }
  }

  private static ParseError unknownFunctionError(Dependency dependency) {
    return new ParseError(dependency.location(),
        "Call to unknown function " + dependency.functionName() + ".");
  }
}
