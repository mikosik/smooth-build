package org.smoothbuild.parse;

import static org.smoothbuild.function.base.Name.isLegalName;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenImportError;

import com.google.common.collect.Maps;

/**
 * Transforms script ParseTree into map that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector {

  public static Map<String, FunctionContext> collectFunctions(MessageGroup messages,
      SymbolTable importedFunctions, ModuleContext module) {
    Worker worker = new Worker(messages, importedFunctions);
    worker.visit(module);
    messages.failIfContainsProblems();
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final SymbolTable importedFunctions;
    private final MessageGroup messages;
    private final Map<String, FunctionContext> functions;

    @Inject
    public Worker(MessageGroup messages, SymbolTable importedFunctions) {
      this.importedFunctions = importedFunctions;
      this.messages = messages;
      this.functions = Maps.newHashMap();
    }

    @Override
    public Void visitFunction(FunctionContext functionContext) {
      FunctionNameContext nameContext = functionContext.functionName();
      String name = nameContext.getText();

      if (!isLegalName(name)) {
        messages.report(new IllegalFunctionNameError(locationOf(nameContext), name));
        return null;
      }

      if (functions.keySet().contains(name)) {
        messages.report(new DuplicateFunctionError(locationOf(nameContext), name));
        return null;
      }
      if (importedFunctions.containsFunction(name)) {
        Name importedName = importedFunctions.getFunction(name).name();
        CodeLocation location = locationOf(nameContext);
        messages.report(new OverridenImportError(location, name, importedName));
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
