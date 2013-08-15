package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.FullyQualifiedName.isValidSimpleName;

import java.util.Map;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.lang.function.FullyQualifiedName;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenImportWarning;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.problem.SourceLocation;
import org.smoothbuild.registry.ImportedFunctions;

import com.google.common.collect.Maps;

/**
 * Transforms script ParseTree into map that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector extends SmoothBaseVisitor<Void> {
  private final ProblemsListener problemsListener;
  private final ImportedFunctions importedFunctions;
  private final Map<String, FunctionContext> functions;

  public FunctionsCollector(ProblemsListener problemsListener, ImportedFunctions importedFunctions) {
    this.problemsListener = problemsListener;
    this.importedFunctions = importedFunctions;
    this.functions = Maps.newHashMap();
  }

  @Override
  public Void visitFunction(FunctionContext functionContext) {
    FunctionNameContext nameContext = functionContext.functionName();
    String name = nameContext.getText();

    if (!isValidSimpleName(name)) {
      problemsListener.report(new IllegalFunctionNameError(Helpers.locationOf(nameContext), name));
      return null;
    }

    if (functions.keySet().contains(name)) {
      problemsListener.report(new DuplicateFunctionError(Helpers.locationOf(nameContext), name));
      return null;
    }
    if (importedFunctions.contains(name)) {
      FullyQualifiedName importedName = importedFunctions.get(name).name();
      SourceLocation location = Helpers.locationOf(nameContext);
      problemsListener.report(new OverridenImportWarning(location, name, importedName));
      return null;
    }

    functions.put(name, functionContext);
    return null;
  }

  public Map<String, FunctionContext> foundFunctions() {
    return functions;
  }
}
