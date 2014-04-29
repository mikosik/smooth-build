package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.UnknownFunctionCallError;

/**
 * Detects calls to functions that are neither declared nor imported.
 */
public class UnknownFunctionCallsDetector {
  public static void detectUndefinedFunctions(LoggedMessages messages, Module builtinModule,
      Map<Name, Set<Dependency>> dependencies) {

    Set<Name> declaredFunctions = dependencies.keySet();

    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        Name name = dependency.functionName();
        if (!builtinModule.containsFunction(name) && !declaredFunctions.contains(name)) {
          messages.log(new UnknownFunctionCallError(dependency.location(), name));
        }
      }
    }
    messages.failIfContainsProblems();
  }
}
