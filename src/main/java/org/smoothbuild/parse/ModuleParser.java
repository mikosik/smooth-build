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
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.ImmutableModule;
import org.smoothbuild.lang.module.Module;

public class ModuleParser {
  private final FileSystem fileSystem;
  private final Module builtinModule;
  private final DefinedFunctionsCreator definedFunctionsCreator;
  private final Console console;

  @Inject
  public ModuleParser(@ProjectDir FileSystem fileSystem, @Builtin Module builtinModule,
      DefinedFunctionsCreator definedFunctionsCreator, Console console) {
    this.fileSystem = fileSystem;
    this.builtinModule = builtinModule;
    this.definedFunctionsCreator = definedFunctionsCreator;
    this.console = console;
  }

  public Module createModule() {
    InputStream inputStream = scriptInputStream(DEFAULT_SCRIPT);
    return createModule(inputStream, DEFAULT_SCRIPT);
  }

  private InputStream scriptInputStream(Path scriptFile) {
    try {
      return fileSystem.openInputStream(scriptFile);
    } catch (NoSuchFileError e) {
      console.error("Cannot find build script file " + scriptFile + ".");
      throw new ParsingException();
    }
  }

  private Module createModule(InputStream inputStream, Path scriptFile) {
    ModuleContext module = parseScript(console, inputStream, scriptFile);
    Map<Name, FunctionContext> functions = collectFunctions(console, builtinModule, module);
    Map<Name, Set<Dependency>> dependencies = collectDependencies(module);
    detectUndefinedFunctions(console, builtinModule, dependencies);
    List<Name> sorted = sortDependencies(builtinModule, dependencies, console);

    Map<Name, Function> definedFunctions = definedFunctionsCreator.createDefinedFunctions(
        console, builtinModule, functions, sorted);

    return new ImmutableModule(definedFunctions);
  }

  public static void detectUndefinedFunctions(Console console, Module builtinModule,
      Map<Name, Set<Dependency>> dependencies) {
    Set<Name> declaredFunctions = dependencies.keySet();
    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        Name name = dependency.functionName();
        if (!builtinModule.containsFunction(name) && !declaredFunctions.contains(name)) {
          console.error(dependency.location(), "Call to unknown function " + name + ".");
        }
      }
    }
    if (console.isProblemReported()) {
      throw new ParsingException();
    }
  }
}
