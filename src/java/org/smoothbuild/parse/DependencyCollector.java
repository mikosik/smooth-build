package org.smoothbuild.parse;

import static org.smoothbuild.parse.Helpers.locationOf;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionCallContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.problem.SourceLocation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Collects functions' dependencies (names of functions that are called within
 * given function).
 */
public class DependencyCollector extends SmoothBaseVisitor<Void> {
  private final ImmutableMap.Builder<String, Set<Dependency>> dependencies;
  private ImmutableSet.Builder<Dependency> currentFunctionDependencies;

  public DependencyCollector(ProblemsListener problemsListener) {
    this.dependencies = ImmutableMap.builder();
  }

  @Override
  public Void visitFunction(FunctionContext function) {
    currentFunctionDependencies = ImmutableSet.builder();
    String name = function.functionName().getText();

    visitChildren(function);

    dependencies.put(name, currentFunctionDependencies.build());

    return null;
  }

  @Override
  public Void visitFunctionCall(FunctionCallContext functionCall) {
    FunctionNameContext functionName = functionCall.functionName();
    String name = functionName.getText();
    SourceLocation location = locationOf(functionName);
    currentFunctionDependencies.add(new Dependency(location, name));

    return visitChildren(functionCall);
  }

  public Map<String, Set<Dependency>> dependencies() {
    return dependencies.build();
  }
}
