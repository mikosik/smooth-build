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
import org.smoothbuild.cli.CommandFailedException;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.ImmutableModule;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.parse.err.ScriptFileNotFoundError;

public class ModuleParser {
  private final FileSystem fileSystem;
  private final Module builtinModule;
  private final DefinedFunctionsCreator definedFunctionsCreator;
  private final ParsingMessages parsingMessages;

  @Inject
  public ModuleParser(@ProjectDir FileSystem fileSystem, @Builtin Module builtinModule,
      DefinedFunctionsCreator definedFunctionsCreator, ParsingMessages parsingMessages) {
    this.fileSystem = fileSystem;
    this.builtinModule = builtinModule;
    this.definedFunctionsCreator = definedFunctionsCreator;
    this.parsingMessages = parsingMessages;
  }

  public Module createModule() {
    InputStream inputStream = scriptInputStream(DEFAULT_SCRIPT);
    return createModule(inputStream, DEFAULT_SCRIPT);
  }

  private InputStream scriptInputStream(Path scriptFile) {
    try {
      return fileSystem.openInputStream(scriptFile);
    } catch (NoSuchFileError e) {
      throw new ScriptFileNotFoundError(scriptFile);
    }
  }

  private Module createModule(InputStream inputStream, Path scriptFile) {
    ModuleContext module = parseScript(parsingMessages, inputStream, scriptFile);
    Map<Name, FunctionContext> functions = collectFunctions(parsingMessages, builtinModule, module);
    Map<Name, Set<Dependency>> dependencies = collectDependencies(module);
    detectUndefinedFunctions(parsingMessages, builtinModule, dependencies);
    List<Name> sorted = sortDependencies(builtinModule, dependencies);

    Map<Name, Function> definedFunctions = definedFunctionsCreator.createDefinedFunctions(
        parsingMessages, builtinModule, functions, sorted);

    return new ImmutableModule(definedFunctions);
  }

  public static void detectUndefinedFunctions(ParsingMessages parsingMessages, Module builtinModule,
      Map<Name, Set<Dependency>> dependencies) {
    Set<Name> declaredFunctions = dependencies.keySet();
    for (Set<Dependency> functionDependecies : dependencies.values()) {
      for (Dependency dependency : functionDependecies) {
        Name name = dependency.functionName();
        if (!builtinModule.containsFunction(name) && !declaredFunctions.contains(name)) {
          parsingMessages.error(dependency.location(), "Call to unknown function " + name + ".");
        }
      }
    }
    if (parsingMessages.hasErrors()) {
      throw new CommandFailedException();
    }
  }
}
