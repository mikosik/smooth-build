package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;

/**
 * Transforms script ParseTree into map that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector {
  public static Maybe<Map<Name, FunctionContext>> collectFunctions(Functions functions,
      ModuleContext module) {
    Worker worker = new Worker(functions);
    worker.visit(module);
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final Functions functions;
    private final List<ParseError> errors;
    private final Map<Name, FunctionContext> functionContexts;

    @Inject
    public Worker(Functions functions) {
      this.functions = functions;
      this.errors = new ArrayList<>();
      this.functionContexts = new HashMap<>();
    }

    public Void visitFunction(FunctionContext functionContext) {
      FunctionNameContext nameContext = functionContext.functionName();
      Name name = name(nameContext.getText());
      if (functionContexts.keySet().contains(name)) {
        errors.add(new ParseError(
            locationOf(nameContext), "Function " + name + " is already defined."));
        return null;
      }
      if (functions.contains(name)) {
        errors.add(new ParseError(locationOf(nameContext), "Function " + name
            + " cannot override builtin function with the same name."));
        return null;
      }

      functionContexts.put(name, functionContext);
      return null;
    }

    public Maybe<Map<Name, FunctionContext>> result() {
      return Maybe.result(functionContexts).addErrors(errors);
    }
  }
}
