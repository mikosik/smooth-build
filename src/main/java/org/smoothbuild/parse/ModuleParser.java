package org.smoothbuild.parse;

import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.FunctionsCollector.collectFunctions;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.parse.UnknownFunctionCallsDetector.detectUndefinedFunctions;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.cli.work.build.CommandLineArguments;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.ImmutableModule;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.ScriptFileNotFoundError;

public class ModuleParser {
  private final FileSystem fileSystem;
  private final ModuleParserMessages messages;
  private final Module builtinModule;
  private final DefinedFunctionsCreator definedFunctionsCreator;

  @Inject
  public ModuleParser(@ProjectDir FileSystem fileSystem, ModuleParserMessages messages,
      @Builtin Module builtinModule, DefinedFunctionsCreator definedFunctionsCreator) {
    this.fileSystem = fileSystem;
    this.messages = messages;
    this.builtinModule = builtinModule;
    this.definedFunctionsCreator = definedFunctionsCreator;
  }

  public Module createModule(CommandLineArguments args) {
    Path scriptFile = args.scriptFile();
    InputStream inputStream = scriptInputStream(scriptFile);

    return createModule(messages, inputStream, scriptFile);
  }

  private InputStream scriptInputStream(Path scriptFile) {
    try {
      return fileSystem.openInputStream(scriptFile);
    } catch (NoSuchFileError e) {
      throw new ScriptFileNotFoundError(scriptFile);
    }
  }

  private Module createModule(LoggedMessages loggedMessages, InputStream inputStream,
      Path scriptFile) {
    ModuleContext module = parseScript(loggedMessages, inputStream, scriptFile);

    Map<Name, FunctionContext> functions = collectFunctions(loggedMessages, builtinModule, module);

    Map<Name, Set<Dependency>> dependencies = collectDependencies(loggedMessages, module);
    detectUndefinedFunctions(loggedMessages, builtinModule, dependencies);
    List<Name> sorted = sortDependencies(builtinModule, dependencies);

    Map<Name, Function<?>> definedFunctions =
        definedFunctionsCreator.createDefinedFunctions(loggedMessages, builtinModule, functions,
            sorted);

    return new ImmutableModule(definedFunctions);
  }
}
