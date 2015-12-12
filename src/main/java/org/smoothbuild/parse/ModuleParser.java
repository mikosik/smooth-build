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
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

public class ModuleParser {
  private final FileSystem fileSystem;
  private final Map<Name, Function> builtinModule;
  private final DefinedFunctionsCreator definedFunctionsCreator;
  private final Console console;

  @Inject
  public ModuleParser(@ProjectDir FileSystem fileSystem, Map<Name, Function> builtinModule,
      DefinedFunctionsCreator definedFunctionsCreator, Console console) {
    this.fileSystem = fileSystem;
    this.builtinModule = builtinModule;
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
    Map<Name, FunctionContext> functions = collectFunctions(console, builtinModule, module);
    Map<Name, Set<Dependency>> dependencies = collectDependencies(module);
    detectUndefinedFunctions(console, builtinModule, dependencies);
    List<Name> sorted = sortDependencies(builtinModule, dependencies, console);

    Map<Name, Function> definedFunctions = definedFunctionsCreator.createDefinedFunctions(
        console, builtinModule, functions, sorted);

    return definedFunctions;
  }

  public static void detectUndefinedFunctions(Console console, Map<Name, Function> builtinModule,
      Map<Name, Set<Dependency>> dependencies) {
    Set<Name> declaredFunctions = dependencies.keySet();
    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        Name name = dependency.functionName();
        if (!builtinModule.containsKey(name) && !declaredFunctions.contains(name)) {
          console.error(dependency.location(), "Call to unknown function " + name + ".");
        }
      }
    }
    if (console.isErrorReported()) {
      throw new ParsingException();
    }
  }
}
