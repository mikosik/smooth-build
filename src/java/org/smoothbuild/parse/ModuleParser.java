package org.smoothbuild.parse;

import static org.smoothbuild.parse.DependencyCollector.collectDependencies;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.FunctionsCollector.collectFunctions;
import static org.smoothbuild.parse.ScriptParser.parseScript;

import java.io.IOException;
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
import org.smoothbuild.problem.ProblemsListener;

public class ModuleParser {
  private final ImportedFunctions importedFunctions;
  private final DefinedFunctionBuilder definedFunctionBuilder;

  @Inject
  public ModuleParser(ImportedFunctions importedFunctions,
      DefinedFunctionBuilder definedFunctionBuilder) {
    this.importedFunctions = importedFunctions;
    this.definedFunctionBuilder = definedFunctionBuilder;
  }

  public Module createModule(ProblemsListener problemsListener, InputStream inputStream)
      throws IOException {
    ModuleContext module = parseScript(problemsListener, inputStream);
    if (problemsListener.hasAnyProblem()) {
      return null;
    }

    Map<String, FunctionContext> functions = collectFunctions(problemsListener, importedFunctions,
        module);
    if (problemsListener.hasAnyProblem()) {
      return null;
    }

    Map<String, Set<Dependency>> dependencies = collectDependencies(module);

    List<String> sorted = sortDependencies(problemsListener, importedFunctions, dependencies);
    if (problemsListener.hasAnyProblem()) {
      return null;
    }

    Map<QualifiedName, DefinedFunction> definedFunctions = definedFunctionBuilder.build(functions,
        sorted);
    if (problemsListener.hasAnyProblem()) {
      return null;
    }

    return new Module(definedFunctions);
  }
}
