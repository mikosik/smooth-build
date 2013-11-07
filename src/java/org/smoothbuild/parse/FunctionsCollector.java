package org.smoothbuild.parse;

import static org.smoothbuild.function.base.Name.isLegalName;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenBuiltinFunctionError;

import com.google.common.collect.Maps;

/**
 * Transforms script ParseTree into map that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector {

  public static Map<Name, FunctionContext> collectFunctions(MessageGroup messages,
      Module builtinModule, ModuleContext module) {
    Worker worker = new Worker(messages, builtinModule);
    worker.visit(module);
    messages.failIfContainsProblems();
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final Module builtinModule;
    private final MessageGroup messages;
    private final Map<Name, FunctionContext> functions;

    @Inject
    public Worker(MessageGroup messages, Module builtinModule) {
      this.builtinModule = builtinModule;
      this.messages = messages;
      this.functions = Maps.newHashMap();
    }

    @Override
    public Void visitFunction(FunctionContext functionContext) {
      FunctionNameContext nameContext = functionContext.functionName();
      String nameString = nameContext.getText();

      if (!isLegalName(nameString)) {
        messages.report(new IllegalFunctionNameError(locationOf(nameContext), nameString));
        return null;
      }

      Name name = Name.name(nameString);
      if (functions.keySet().contains(name)) {
        messages.report(new DuplicateFunctionError(locationOf(nameContext), name));
        return null;
      }
      if (builtinModule.containsFunction(name)) {
        CodeLocation location = locationOf(nameContext);
        messages.report(new OverridenBuiltinFunctionError(location, name));
        return null;
      }

      functions.put(name, functionContext);
      return null;
    }

    public Map<Name, FunctionContext> result() {
      return functions;
    }
  }
}
