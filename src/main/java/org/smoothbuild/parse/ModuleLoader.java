package org.smoothbuild.parse;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.parse.DefinedFunctionLoader.loadDefinedFunction;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.FunctionsCollector.collectFunctions;
import static org.smoothbuild.parse.Maybe.error;
import static org.smoothbuild.parse.Maybe.errors;
import static org.smoothbuild.parse.Maybe.invoke;
import static org.smoothbuild.parse.Maybe.invokeWrap;
import static org.smoothbuild.parse.Maybe.result;
import static org.smoothbuild.parse.ScriptParser.parseScript;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.def.DefinedFunction;

public class ModuleLoader {
  private final FileSystem fileSystem;

  @Inject
  public ModuleLoader(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Maybe<Functions> loadFunctions(Functions functions, Path smoothFile) {
    Maybe<InputStream> inputStream = scriptInputStream(smoothFile);
    return invoke(inputStream, is -> loadFunctions(functions, is, smoothFile));
  }

  private Maybe<InputStream> scriptInputStream(Path scriptFile) {
    try {
      return result(fileSystem.openInputStream(scriptFile));
    } catch (FileSystemException e) {
      return error("error: Cannot read build script file " + scriptFile + ". " + e.getMessage());
    }
  }

  private Maybe<Functions> loadFunctions(Functions functions, InputStream inputStream,
      Path scriptFile) {
    Maybe<ModuleContext> module = parseScript(inputStream, scriptFile);
    Maybe<Map<Name, FunctionContext>> functionContexts = invoke(
        module, m -> collectFunctions(functions, m));
    if (!functionContexts.hasResult()) {
      return errors(functionContexts.errors());
    }
    Maybe<Map<Name, Set<Dependency>>> dependencies = invoke(
        module, m -> collectDependencies(m, functions));
    Maybe<List<Name>> sorted = invoke(dependencies, ds -> sortDependencies(functions, ds));
    Maybe<List<FunctionContext>> sortedFunctionContexts =
        invokeWrap(functionContexts, sorted, (fcs, s) -> sortFunctions(fcs, s));
    return invoke(sortedFunctionContexts, sfcs -> loadDefinedFunctions(functions, sfcs));
  }

  private List<FunctionContext> sortFunctions(Map<Name, FunctionContext> functionContexts,
      List<Name> names) {
    return names.stream()
        .map(n -> functionContexts.get(n))
        .collect(toList());
  }

  private Maybe<Functions> loadDefinedFunctions(Functions functions,
      List<FunctionContext> functionContexts) {
    Maybe<Functions> justLoaded = result(new Functions());
    for (FunctionContext functionContext : functionContexts) {
      Maybe<Functions> all = invokeWrap(justLoaded, (j) -> j.addAll(functions));
      Maybe<DefinedFunction> function = invoke(all,
          a -> loadDefinedFunction(a, functionContext));
      justLoaded = invokeWrap(justLoaded, function, Functions::add);
    }
    return justLoaded;
  }
}
