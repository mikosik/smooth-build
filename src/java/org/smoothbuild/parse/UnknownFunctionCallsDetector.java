package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.parse.err.UnknownFunctionCallError;

/**
 * Detects calls to functions that are neither declared nor imported.
 */
public class UnknownFunctionCallsDetector {
  public static void detectUndefinedFunctions(MessageGroup messages, Module builtinModule,
      Map<Name, Set<Dependency>> dependencies) {

    Set<Name> declaredFunctions = dependencies.keySet();

    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        Name name = dependency.functionName();
        if (!builtinModule.containsFunction(name) && !declaredFunctions.contains(name)) {
          messages.report(new UnknownFunctionCallError(dependency.location(), name));
        }
      }
    }
    messages.failIfContainsProblems();
  }
}
