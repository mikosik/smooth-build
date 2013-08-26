package org.smoothbuild.parse;

import static org.smoothbuild.parse.Helpers.locationOf;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.CallContext;
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

  @Inject
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
  public Void visitCall(CallContext call) {
    FunctionNameContext functionName = call.functionName();
    String name = functionName.getText();
    SourceLocation location = locationOf(functionName);
    currentFunctionDependencies.add(new Dependency(location, name));

    return visitChildren(call);
  }

  public Map<String, Set<Dependency>> dependencies() {
    return dependencies.build();
  }
}
