package org.smoothbuild.parse;

import static org.smoothbuild.function.base.QualifiedName.isValidSimpleName;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.function.base.QualifiedName;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenImportWarning;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.problem.SourceLocation;

import com.google.common.collect.Maps;

/**
 * Transforms script ParseTree into map that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector {

  public static Map<String, FunctionContext> collectFunctions(ProblemsListener problemsListener,
      SymbolTable importedFunctions, ModuleContext module) {
    Worker worker = new Worker(problemsListener, importedFunctions);
    worker.visit(module);
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final SymbolTable importedFunctions;
    private final ProblemsListener problemsListener;
    private final Map<String, FunctionContext> functions;

    @Inject
    public Worker(ProblemsListener problemsListener, SymbolTable importedFunctions) {
      this.importedFunctions = importedFunctions;
      this.problemsListener = problemsListener;
      this.functions = Maps.newHashMap();
    }

    @Override
    public Void visitFunction(FunctionContext functionContext) {
      FunctionNameContext nameContext = functionContext.functionName();
      String name = nameContext.getText();

      if (!isValidSimpleName(name)) {
        problemsListener
            .report(new IllegalFunctionNameError(Helpers.locationOf(nameContext), name));
        return null;
      }

      if (functions.keySet().contains(name)) {
        problemsListener.report(new DuplicateFunctionError(Helpers.locationOf(nameContext), name));
        return null;
      }
      if (importedFunctions.containsFunction(name)) {
        QualifiedName importedName = importedFunctions.getFunction(name).name();
        SourceLocation location = Helpers.locationOf(nameContext);
        problemsListener.report(new OverridenImportWarning(location, name, importedName));
        return null;
      }

      functions.put(name, functionContext);
      return null;
    }

    public Map<String, FunctionContext> result() {
      return functions;
    }
  }
}
