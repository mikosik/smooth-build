package org.smoothbuild.parse;

import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.FunctionsCollector.collectFunctions;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.parse.UndefinedFunctionsDetector.detectUndefinedFunctions;
import static org.smoothbuild.parse.def.DefinedFunctionsCreator.createDefinedFunctions;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.plugin.api.Path;

public class ModuleParser {
  private final ImportedFunctions importedFunctions;

  @Inject
  public ModuleParser(ImportedFunctions importedFunctions) {
    this.importedFunctions = importedFunctions;
  }

  public Module createModule(MessageListener messageListener, InputStream inputStream,
      Path scriptFile) {
    DetectingErrorsMessageListener messages = new DetectingErrorsMessageListener(messageListener);
    return createModule(messages, inputStream, scriptFile);
  }

  private Module createModule(DetectingErrorsMessageListener messages, InputStream inputStream,
      Path scriptFile) {
    ModuleContext module = parseScript(messages, inputStream, scriptFile);
    if (messages.errorDetected()) {
      return null;
    }

    Map<String, FunctionContext> functions = collectFunctions(messages, importedFunctions, module);
    if (messages.errorDetected()) {
      return null;
    }

    Map<String, Set<Dependency>> dependencies = collectDependencies(module);

    detectUndefinedFunctions(messages, importedFunctions, dependencies);
    if (messages.errorDetected()) {
      return null;
    }

    List<String> sorted = sortDependencies(messages, importedFunctions, dependencies);
    if (messages.errorDetected()) {
      return null;
    }

    Map<Name, DefinedFunction> definedFunctions = createDefinedFunctions(messages,
        importedFunctions, functions, sorted);
    if (messages.errorDetected()) {
      return null;
    }

    return new Module(definedFunctions);
  }
}
