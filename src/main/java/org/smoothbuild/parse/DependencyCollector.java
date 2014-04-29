package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.isLegalName;
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
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.IllegalFunctionNameError;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Collects functions' dependencies (names of functions that are called within
 * given function).
 */
public class DependencyCollector {
  public static Map<Name, Set<Dependency>> collectDependencies(LoggedMessages messages,
      ModuleContext moduleContext) {
    Worker worker = new Worker(messages);
    worker.visit(moduleContext);
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final LoggedMessages messages;
    private final ImmutableMap.Builder<Name, Set<Dependency>> dependencies;
    private ImmutableSet.Builder<Dependency> currentFunctionDependencies;

    public Worker(LoggedMessages loggedMessages) {
      this.messages = loggedMessages;
      this.dependencies = ImmutableMap.builder();
    }

    @Override
    public Void visitFunction(FunctionContext function) {
      currentFunctionDependencies = ImmutableSet.builder();
      String nameString = function.functionName().getText();

      if (!isLegalName(nameString)) {
        messages.log(new IllegalFunctionNameError(locationOf(function), nameString));
        return null;
      }

      Name name = Name.name(nameString);
      visitChildren(function);

      dependencies.put(name, currentFunctionDependencies.build());

      return null;
    }

    @Override
    public Void visitCall(CallContext call) {
      FunctionNameContext functionName = call.functionName();
      String nameString = functionName.getText();
      if (!isLegalName(nameString)) {
        messages.log(new IllegalFunctionNameError(locationOf(call), nameString));
        return null;
      }

      Name name = name(nameString);
      CodeLocation location = locationOf(functionName);
      currentFunctionDependencies.add(new Dependency(location, name));

      return visitChildren(call);
    }

    public Map<Name, Set<Dependency>> result() {
      return dependencies.build();
    }
  }
}
