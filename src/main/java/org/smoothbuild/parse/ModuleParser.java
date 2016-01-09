package org.smoothbuild.parse;

import static org.smoothbuild.SmoothConstants.DEFAULT_SCRIPT;
import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.FunctionsCollector.collectFunctions;
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
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

public class ModuleParser {
  private final FileSystem fileSystem;
  private final Functions functions;
  private final DefinedFunctionsCreator definedFunctionsCreator;
  private final Console console;

  @Inject
  public ModuleParser(FileSystem fileSystem, Functions functions,
      DefinedFunctionsCreator definedFunctionsCreator, Console console) {
    this.fileSystem = fileSystem;
    this.functions = functions;
    this.definedFunctionsCreator = definedFunctionsCreator;
    this.console = console;
  }

  public Map<Name, Function> createModule() {
    InputStream inputStream = scriptInputStream(DEFAULT_SCRIPT);
    return createModule(inputStream, DEFAULT_SCRIPT);
  }

  private InputStream scriptInputStream(Path scriptFile) {
    try {
      return fileSystem.openInputStream(scriptFile);
    } catch (FileSystemException e) {
      console.error("Cannot read build script file " + scriptFile + ". " + e.getMessage());
      throw new ParsingException();
    }
  }

  private Map<Name, Function> createModule(InputStream inputStream, Path scriptFile) {
    ModuleContext module = parseScript(console, inputStream, scriptFile);
    Map<Name, FunctionContext> functionContexts = collectFunctions(console, functions, module);
    Map<Name, Set<Dependency>> dependencies = collectDependencies(module);
    detectUndefinedFunctions(console, functions, dependencies);
    List<Name> sorted = sortDependencies(functions, dependencies, console);

    Map<Name, Function> definedFunctions = definedFunctionsCreator.createDefinedFunctions(
        console, functions, functionContexts, sorted);

    return definedFunctions;
  }

  public static void detectUndefinedFunctions(Console console, Functions functions,
      Map<Name, Set<Dependency>> dependencies) {
    Set<Name> declaredFunctions = dependencies.keySet();
    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
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
