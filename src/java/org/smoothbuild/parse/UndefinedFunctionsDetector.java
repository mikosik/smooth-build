package org.smoothbuild.parse;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.parse.err.UndefinedFunctionError;
import org.smoothbuild.problem.ProblemsListener;

/**
 * Detects calls to functions that are neither declared nor imported.
 */
public class UndefinedFunctionsDetector {
  public void detect(SymbolTable importedFunctions, Map<String, Set<Dependency>> dependencies,
      ProblemsListener problemsListener) {

    Set<String> declaredFunctions = dependencies.keySet();

    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        String name = dependency.functionName();
        if (!importedFunctions.containsFunction(name) && !declaredFunctions.contains(name)) {
          problemsListener.report(new UndefinedFunctionError(dependency.location(), name));
        }
      }
    }
  }
}
