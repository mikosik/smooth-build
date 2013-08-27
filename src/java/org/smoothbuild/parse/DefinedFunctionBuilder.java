package org.smoothbuild.parse;

import static org.smoothbuild.function.base.QualifiedName.simpleName;
import static org.smoothbuild.function.expr.LiteralExpression.stringExpression;
import static org.smoothbuild.parse.Helpers.locationOf;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.function.def.ExpressionNode;
import org.smoothbuild.function.def.FunctionNode;
import org.smoothbuild.function.def.InvalidNode;
import org.smoothbuild.function.expr.ExpressionId;
import org.smoothbuild.function.expr.ExpressionIdFactory;
import org.smoothbuild.problem.SourceLocation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

// TODO test it
public class DefinedFunctionBuilder {
  private final SymbolTable symbolTable;
  private final ExpressionIdFactory expressionIdFactory;
  private final ArgumentListBuilder builder;

  private final Map<String, DefinedFunction> functions;

  @Inject
  public DefinedFunctionBuilder(SymbolTable symbolTable, ExpressionIdFactory expressionIdFactory,
      ArgumentListBuilder builder) {
    this.symbolTable = symbolTable;
    this.expressionIdFactory = expressionIdFactory;
    this.builder = builder;

    this.functions = Maps.newHashMap();
  }

  public Map<String, DefinedFunction> build(Map<String, FunctionContext> functionContexts,
      List<String> sorted) {
    for (String name : sorted) {
      DefinedFunction definedFunction = build(functionContexts.get(name));
      functions.put(name, definedFunction);
    }
    return functions;
  }

  public DefinedFunction build(FunctionContext function) {
    DefinitionNode node = build(function.pipe());

    Type type = node.type();
    String name = function.functionName().getText();
    ImmutableMap<String, Param> params = ImmutableMap.<String, Param> of();
    Signature signature = new Signature(type, simpleName(name), params);

    return new DefinedFunction(signature, node);
  }

  private DefinitionNode build(PipeContext pipe) {
    DefinitionNode result = build(pipe.expression());
    List<CallContext> elements = pipe.call();
    for (int i = 0; i < elements.size(); i++) {
      CallContext call = elements.get(i);
      List<Argument> arguments = build(call.argList());
      // implicit piped argument's location is set to the pipe character '|'
      SourceLocation sourceLocation = locationOf(pipe.p.get(i));
      arguments.add(new Argument(null, result, sourceLocation));
      result = build(call, arguments);
    }
    return result;
  }

  private DefinitionNode build(ExpressionContext expression) {
    if (expression.call() != null) {
      return build(expression.call());
    }
    return buildStringNode(expression.STRING());
  }

  private DefinitionNode build(CallContext call) {
    List<Argument> arguments = build(call.argList());
    return build(call, arguments);
  }

  private DefinitionNode build(CallContext call, List<Argument> args) {
    String functionName = call.functionName().getText();

    Function function = getFunction(functionName);

    Map<String, DefinitionNode> explicitArgs = builder.convert(function, args);

    if (explicitArgs == null) {
      return new InvalidNode(function.type());
    } else {
      return new FunctionNode(function, explicitArgs);
    }
  }

  private Function getFunction(String functionName) {
    // UndefinedFunctionDetector has been run already so we can be sure at this
    // point that function with given name exists either among imported
    // functions or among already handled defined functions.

    Function function = symbolTable.getFunction(functionName);
    if (function == null) {
      return functions.get(functionName);
    } else {
      return function;
    }
  }

  private List<Argument> build(ArgListContext argList) {
    List<Argument> result = Lists.newArrayList();
    if (argList != null) {
      for (ArgContext arg : argList.arg()) {
        DefinitionNode node = build(arg.expression());
        result.add(new Argument(argName(arg), node, argLocation(arg)));
      }
    }
    return result;
  }

  private static String argName(ArgContext arg) {
    ParamNameContext paramName = arg.paramName();
    if (paramName == null) {
      return null;
    } else {
      return paramName.getText();
    }
  }

  private static SourceLocation argLocation(ArgContext arg) {
    ParamNameContext paramName = arg.paramName();
    if (paramName == null) {
      return locationOf(arg.expression());
    } else {
      return locationOf(paramName);
    }
  }

  private DefinitionNode buildStringNode(TerminalNode stringToken) {
    String quotedString = stringToken.getText();
    String string = quotedString.substring(1, quotedString.length() - 1);
    ExpressionId id = expressionIdFactory.createId(string);
    return new ExpressionNode(stringExpression(id, string));
  }
}
