package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenBuiltinFunctionError;

import com.google.common.collect.Maps;

/**
 * Transforms script ParseTree into paramsToMap that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector {

  public static Map<Name, FunctionContext> collectFunctions(LoggedMessages messages,
      Module builtinModule, ModuleContext module) {
    Worker worker = new Worker(messages, builtinModule);
    worker.visit(module);
    messages.failIfContainsProblems();
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final Module builtinModule;
    private final LoggedMessages messages;
    private final Map<Name, FunctionContext> functions;

    @Inject
    public Worker(LoggedMessages messages, Module builtinModule) {
      this.builtinModule = builtinModule;
      this.messages = messages;
      this.functions = Maps.newHashMap();
    }

    @Override
    public Void visitFunction(FunctionContext functionContext) {
      FunctionNameContext nameContext = functionContext.functionName();
      String nameString = nameContext.getText();

      if (!isLegalName(nameString)) {
        messages.log(new IllegalFunctionNameError(locationOf(nameContext), nameString));
        return null;
      }

      Name name = Name.name(nameString);
      if (functions.keySet().contains(name)) {
        messages.log(new DuplicateFunctionError(locationOf(nameContext), name));
        return null;
      }
      if (builtinModule.containsFunction(name)) {
        CodeLocation location = locationOf(nameContext);
        messages.log(new OverridenBuiltinFunctionError(location, name));
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
