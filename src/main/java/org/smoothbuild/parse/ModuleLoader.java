package org.smoothbuild.parse;

import static org.smoothbuild.parse.DefinedFunctionLoader.loadDefinedFunction;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.FunctionsCollector.collectFunctions;
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
import org.smoothbuild.cli.Console;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.def.DefinedFunction;

public class ModuleLoader {
  private final FileSystem fileSystem;
  private final Console console;

  @Inject
  public ModuleLoader(FileSystem fileSystem, Console console) {
    this.fileSystem = fileSystem;
    this.console = console;
  }

  public Maybe<Functions> loadFunctions(Functions functions, Path smoothFile) {
    return loadFunctions(functions, scriptInputStream(smoothFile), smoothFile);
  }

  private InputStream scriptInputStream(Path scriptFile) {
    try {
      return fileSystem.openInputStream(scriptFile);
    } catch (FileSystemException e) {
      console.error("Cannot read build script file " + scriptFile + ". " + e.getMessage());
      throw new ParsingException();
    }
  }

  private Maybe<Functions> loadFunctions(Functions functions, InputStream inputStream,
      Path scriptFile) {
    ModuleContext module = parseScript(console, inputStream, scriptFile);
    Maybe<Map<Name, FunctionContext>> functionContexts = collectFunctions(functions, module);
    if (!functionContexts.hasResult()) {
      return Maybe.errors(functionContexts.errors());
    }
    Maybe<Map<Name, Set<Dependency>>> dependencies = collectDependencies(module, functions);
    Maybe<List<Name>> sorted = invoke(dependencies,
        dependencies_ -> sortDependencies(functions, dependencies_));
    return loadDefinedFunctions(functions, functionContexts, sorted);
  }

  public Maybe<Functions> loadDefinedFunctions(Functions functions,
      Maybe<Map<Name, FunctionContext>> functionContexts,
      Maybe<List<Name>> sorted) {
    return invoke(functionContexts, sorted, (fc, s) -> {
      Maybe<Functions> justLoaded = result(new Functions());
      for (Name name : s) {
        Maybe<Functions> all = invokeWrap(justLoaded, (j) -> j.addAll(functions));
        Maybe<DefinedFunction> function = invoke(all,
            a -> loadDefinedFunction(a, fc.get(name)));
        justLoaded = invokeWrap(justLoaded, function, Functions::add);
      }
      return justLoaded;
    });
  }
}
