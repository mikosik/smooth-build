package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.parse.err.UndefinedFunctionError;

/**
 * Detects calls to functions that are neither declared nor imported.
 */
public class UndefinedFunctionsDetector {
  public static void detectUndefinedFunctions(MessageListener messages,
      SymbolTable importedFunctions, Map<String, Set<Dependency>> dependencies) {

    Set<String> declaredFunctions = dependencies.keySet();

    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        String name = dependency.functionName();
        if (!importedFunctions.containsFunction(name) && !declaredFunctions.contains(name)) {
          messages.report(new UndefinedFunctionError(dependency.location(), name));
        }
      }
    }
  }
}
