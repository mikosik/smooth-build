package org.smoothbuild.parse;

import static org.smoothbuild.function.base.Name.isLegalSimpleName;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenImportError;
import org.smoothbuild.problem.CodeLocation;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.Maps;

/**
 * Transforms script ParseTree into map that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector {

  public static Map<String, FunctionContext> collectFunctions(ProblemsListener problems,
      SymbolTable importedFunctions, ModuleContext module) {
    Worker worker = new Worker(problems, importedFunctions);
    worker.visit(module);
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final SymbolTable importedFunctions;
    private final ProblemsListener problems;
    private final Map<String, FunctionContext> functions;

    @Inject
    public Worker(ProblemsListener problems, SymbolTable importedFunctions) {
      this.importedFunctions = importedFunctions;
      this.problems = problems;
      this.functions = Maps.newHashMap();
    }

    @Override
    public Void visitFunction(FunctionContext functionContext) {
      FunctionNameContext nameContext = functionContext.functionName();
      String name = nameContext.getText();

      if (!isLegalSimpleName(name)) {
        problems.report(new IllegalFunctionNameError(locationOf(nameContext), name));
        return null;
      }

      if (functions.keySet().contains(name)) {
        problems.report(new DuplicateFunctionError(locationOf(nameContext), name));
        return null;
      }
      if (importedFunctions.containsFunction(name)) {
        Name importedName = importedFunctions.getFunction(name).name();
        CodeLocation location = locationOf(nameContext);
        problems.report(new OverridenImportError(location, name, importedName));
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
