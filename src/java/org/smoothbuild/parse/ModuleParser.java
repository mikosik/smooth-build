package org.smoothbuild.parse;

import static org.smoothbuild.parse.DefinedFunctionsCreator.createDefinedFunctions;
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
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.DetectingErrorsProblemsListener;
import org.smoothbuild.problem.ProblemsListener;

public class ModuleParser {
  private final ImportedFunctions importedFunctions;

  @Inject
  public ModuleParser(ImportedFunctions importedFunctions) {
    this.importedFunctions = importedFunctions;
  }

  public Module createModule(ProblemsListener problemsListener, InputStream inputStream,
      Path scriptFile) {
    DetectingErrorsProblemsListener problems = new DetectingErrorsProblemsListener(problemsListener);
    ModuleContext module = parseScript(problemsListener, inputStream, scriptFile);
    if (problems.errorDetected()) {
      return null;
    }

    Map<String, FunctionContext> functions = collectFunctions(problemsListener, importedFunctions,
        module);
    if (problems.errorDetected()) {
      return null;
    }

    Map<String, Set<Dependency>> dependencies = collectDependencies(module);

    detectUndefinedFunctions(problems, importedFunctions, dependencies);
    if (problems.errorDetected()) {
      return null;
    }

    List<String> sorted = sortDependencies(problemsListener, importedFunctions, dependencies);
    if (problems.errorDetected()) {
      return null;
    }

    Map<Name, DefinedFunction> definedFunctions = createDefinedFunctions(problems,
        importedFunctions, functions, sorted);
    if (problems.errorDetected()) {
      return null;
    }

    return new Module(definedFunctions);
  }
}
