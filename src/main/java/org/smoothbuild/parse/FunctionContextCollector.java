package org.smoothbuild.parse;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.FunctionsCollector.collectFunctions;
import static org.smoothbuild.parse.Maybe.errors;
import static org.smoothbuild.parse.Maybe.invoke;
import static org.smoothbuild.parse.Maybe.invokeWrap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;

public class FunctionContextCollector {
  public static Maybe<List<FunctionContext>> collectFunctionContexts(ModuleContext module,
      Functions functions) {
    Maybe<Map<Name, FunctionContext>> functionContexts = collectFunctions(functions, module);
    if (!functionContexts.hasResult()) {
      return errors(functionContexts.errors());
    }
    Maybe<Map<Name, Set<Dependency>>> dependencies = collectDependencies(module, functions);
    Maybe<List<Name>> sorted = invoke(dependencies, ds -> sortDependencies(functions, ds));
    return invokeWrap(functionContexts, sorted, (fcs, s) -> sortFunctions(fcs, s));
  }

  private static List<FunctionContext> sortFunctions(Map<Name, FunctionContext> functionContexts,
      List<Name> names) {
    return names.stream()
        .map(n -> functionContexts.get(n))
        .collect(toList());
  }
}
