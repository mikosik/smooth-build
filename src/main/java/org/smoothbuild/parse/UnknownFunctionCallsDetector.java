package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.cli.CommandFailedException;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;

/**
 * Detects calls to functions that are neither declared nor imported.
 */
public class UnknownFunctionCallsDetector {
  public static void detectUndefinedFunctions(ParsingMessages parsingMessages, Module builtinModule,
      Map<Name, Set<Dependency>> dependencies) {
    Set<Name> declaredFunctions = dependencies.keySet();
    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        Name name = dependency.functionName();
        if (!builtinModule.containsFunction(name) && !declaredFunctions.contains(name)) {
          parsingMessages.error(dependency.location(), "Call to unknown function " + name + ".");
        }
      }
    }
    if (parsingMessages.hasErrors()) {
      throw new CommandFailedException();
    }
  }
}
