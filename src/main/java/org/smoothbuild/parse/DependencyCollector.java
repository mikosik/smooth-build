package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Collects functions' dependencies (names of functions that are called within
 * given function).
 */
public class DependencyCollector {
  public static Map<Name, Set<Dependency>> collectDependencies(ModuleContext moduleContext) {
    Worker worker = new Worker();
    worker.visit(moduleContext);
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final ImmutableMap.Builder<Name, Set<Dependency>> dependencies;
    private ImmutableSet.Builder<Dependency> currentFunctionDependencies;

    public Worker() {
      this.dependencies = ImmutableMap.builder();
    }

    public Void visitFunction(FunctionContext function) {
      currentFunctionDependencies = ImmutableSet.builder();
      Name name = name(function.functionName().getText());
      visitChildren(function);
      dependencies.put(name, currentFunctionDependencies.build());
      return null;
    }

    public Void visitCall(CallContext call) {
      FunctionNameContext functionName = call.functionName();
      Name name = name(functionName.getText());
      CodeLocation location = locationOf(functionName);
      currentFunctionDependencies.add(new Dependency(location, name));
      return visitChildren(call);
    }

    public Map<Name, Set<Dependency>> result() {
      return dependencies.build();
    }
  }
}
