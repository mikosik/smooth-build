package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;

/**
 * Transforms script ParseTree into map that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector {

  public static Map<Name, FunctionContext> collectFunctions(Console console, Functions functions,
      ModuleContext module) {
    Worker worker = new Worker(console, functions);
    worker.visit(module);
    if (console.isErrorReported()) {
      throw new ParsingException();
    }
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final Functions functions;
    private final Console console;
    private final Map<Name, FunctionContext> functionContexts;

    @Inject
    public Worker(Console console, Functions functions) {
      this.console = console;
      this.functions = functions;
      this.functionContexts = new HashMap<>();
    }

    public Void visitFunction(FunctionContext functionContext) {
      FunctionNameContext nameContext = functionContext.functionName();
      Name name = name(nameContext.getText());
      if (functionContexts.keySet().contains(name)) {
        console.error(locationOf(nameContext), "Function " + name + " is already defined.");
        return null;
      }
      if (functions.contains(name)) {
        console.error(locationOf(nameContext), "Function " + name
            + " cannot override builtin function with the same name.");
        return null;
      }

      functionContexts.put(name, functionContext);
      return null;
    }

    public Map<Name, FunctionContext> result() {
      return functionContexts;
    }
  }
}
