package org.smoothbuild.parse;

import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.FunctionsCollector.collectFunctions;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.parse.UndefinedFunctionsDetector.detectUndefinedFunctions;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.parse.err.ScriptFileNotFoundError;

public class ModuleParser {
  private final FileSystem fileSystem;
  private final ScriptParserMessageGroup messages;
  private final ImportedFunctions importedFunctions;
  private final DefinedFunctionsCreator definedFunctionsCreator;

  @Inject
  public ModuleParser(FileSystem fileSystem, ScriptParserMessageGroup messages,
      ImportedFunctions importedFunctions, DefinedFunctionsCreator definedFunctionsCreator) {
    this.fileSystem = fileSystem;
    this.messages = messages;
    this.importedFunctions = importedFunctions;
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
    } catch (NoSuchFileException e) {
      throw new ErrorMessageException(new ScriptFileNotFoundError(scriptFile));
    }
  }

  private Module createModule(MessageGroup messageGroup, InputStream inputStream, Path scriptFile) {
    ModuleContext module = parseScript(messageGroup, inputStream, scriptFile);
    if (messageGroup.containsErrors()) {
      return null;
    }

    Map<String, FunctionContext> functions = collectFunctions(messageGroup, importedFunctions,
        module);
    if (messageGroup.containsErrors()) {
      return null;
    }

    Map<String, Set<Dependency>> dependencies = collectDependencies(module);

    detectUndefinedFunctions(messageGroup, importedFunctions, dependencies);
    if (messageGroup.containsErrors()) {
      return null;
    }

    List<String> sorted = sortDependencies(messageGroup, importedFunctions, dependencies);
    if (messageGroup.containsErrors()) {
      return null;
    }

    Map<Name, DefinedFunction> definedFunctions = definedFunctionsCreator.createDefinedFunctions(
        messageGroup, importedFunctions, functions, sorted);
    if (messageGroup.containsErrors()) {
      return null;
    }

    return new Module(definedFunctions);
  }
}
