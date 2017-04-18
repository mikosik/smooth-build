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
    Map<Name, FunctionContext> functionContexts = collectFunctions(console, functions, module);
    Map<Name, Set<Dependency>> dependencies = collectDependencies(module);
    detectUndefinedFunctions(console, functions, dependencies);
    List<Name> sorted = sortDependencies(functions, dependencies, console);
    return loadDefinedFunctions(functions, functionContexts, sorted);
  }

  public Maybe<Functions> loadDefinedFunctions(Functions functions,
      Map<Name, FunctionContext> functionContexts,
      List<Name> sorted) {
    Maybe<Functions> justLoaded = result(new Functions());
    for (Name name : sorted) {
      Maybe<Functions> all = invokeWrap(justLoaded, (j) -> j.addAll(functions));
      Maybe<DefinedFunction> function = invoke(all, a -> loadDefinedFunction(a, functionContexts
          .get(name)));
      justLoaded = invokeWrap(justLoaded, function, Functions::add);
    }
    return justLoaded;
  }

  public static void detectUndefinedFunctions(Console console, Functions functions,
      Map<Name, Set<Dependency>> dependencies) {
    Set<Name> declaredFunctions = dependencies.keySet();
    for (Set<Dependency> functionDependencies : dependencies.values()) {
      for (Dependency dependency : functionDependencies) {
        Name name = dependency.functionName();
        if (!functions.contains(name) && !declaredFunctions.contains(name)) {
          console.error(dependency.location(), "Call to unknown function " + name + ".");
        }
      }
    }
    if (console.isErrorReported()) {
      throw new ParsingException();
    }
  }
}
