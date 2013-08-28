package org.smoothbuild.parse;

import static org.smoothbuild.parse.DefinedFunctionBuilder.createDefinedFunctions;
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
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.QualifiedName;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.problem.DetectingErrorsProblemsListener;
import org.smoothbuild.problem.ProblemsListener;

public class ModuleParser {
  private final ImportedFunctions importedFunctions;

  @Inject
  public ModuleParser(ImportedFunctions importedFunctions) {
    this.importedFunctions = importedFunctions;
  }

  public Module createModule(ProblemsListener problemsListener, InputStream inputStream,
      String scriptFile) {
    DetectingErrorsProblemsListener problemsDetector = new DetectingErrorsProblemsListener(
        problemsListener);
    ModuleContext module = parseScript(problemsListener, inputStream, scriptFile);
    if (problemsDetector.errorDetected()) {
      return null;
    }

    Map<String, FunctionContext> functions = collectFunctions(problemsListener, importedFunctions,
        module);
    if (problemsDetector.errorDetected()) {
      return null;
    }

    Map<String, Set<Dependency>> dependencies = collectDependencies(module);

    List<String> sorted = sortDependencies(problemsListener, importedFunctions, dependencies);
    if (problemsDetector.errorDetected()) {
      return null;
    }

    Map<QualifiedName, DefinedFunction> definedFunctions = createDefinedFunctions(problemsDetector,
        importedFunctions, functions, sorted);
    if (problemsDetector.errorDetected()) {
      return null;
    }

    return new Module(definedFunctions);
  }
}
