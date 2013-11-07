package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.parse.err.UndefinedFunctionError;

/**
 * Detects calls to functions that are neither declared nor imported.
 */
public class UndefinedFunctionsDetector {
  public static void detectUndefinedFunctions(MessageGroup messages, Module builtinModule,
      Map<Name, Set<Dependency>> dependencies) {

    Set<Name> declaredFunctions = dependencies.keySet();

    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        Name name = dependency.functionName();
        if (!builtinModule.containsFunction(name) && !declaredFunctions.contains(name)) {
          messages.report(new UndefinedFunctionError(dependency.location(), name));
        }
      }
    }
    messages.failIfContainsProblems();
  }
}
