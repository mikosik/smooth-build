package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.cli.CommandFailedException;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.OverridenBuiltinFunctionError;

import com.google.common.collect.Maps;

/**
 * Transforms script ParseTree into map that maps function name to its
 * FunctionContext. Detects illegal function names, duplicate function names,
 * overridden imports.
 */
public class FunctionsCollector {

  public static Map<Name, FunctionContext> collectFunctions(LoggedMessages messages,
      ParsingMessages parsingMessages, Module builtinModule, ModuleContext module) {
    Worker worker = new Worker(messages, parsingMessages, builtinModule);
    worker.visit(module);
    if (parsingMessages.hasErrors()) {
      throw new CommandFailedException();
    }
    messages.failIfContainsProblems();
    return worker.result();
  }

  private static class Worker extends SmoothBaseVisitor<Void> {
    private final Module builtinModule;
    private final LoggedMessages messages;
    private final ParsingMessages parsingMessages;
    private final Map<Name, FunctionContext> functions;

    @Inject
    public Worker(LoggedMessages messages, ParsingMessages parsingMessages, Module builtinModule) {
      this.parsingMessages = parsingMessages;
      this.builtinModule = builtinModule;
      this.messages = messages;
      this.functions = Maps.newHashMap();
    }

    @Override
    public Void visitFunction(FunctionContext functionContext) {
      FunctionNameContext nameContext = functionContext.functionName();
      Name name = name(nameContext.getText());
      if (functions.keySet().contains(name)) {
        parsingMessages.error(locationOf(nameContext), "Function " + name + " is already defined.");
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
