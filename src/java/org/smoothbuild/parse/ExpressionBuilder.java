package org.smoothbuild.parse;

import static org.smoothbuild.expression.LiteralExpression.stringExpression;
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
import org.smoothbuild.expression.Expression;
import org.smoothbuild.expression.ExpressionId;
import org.smoothbuild.expression.ExpressionIdFactory;
import org.smoothbuild.expression.InvalidExpression;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.problem.SourceLocation;

import com.google.common.collect.Lists;

// TODO test it
public class ExpressionBuilder {
  private final SymbolTable symbolTable;
  private final ExpressionIdFactory expressionIdFactory;
  private final ArgumentListBuilder builder;

  @Inject
  public ExpressionBuilder(SymbolTable symbolTable, ExpressionIdFactory expressionIdFactory,
      ArgumentListBuilder builder) {
    this.symbolTable = symbolTable;
    this.expressionIdFactory = expressionIdFactory;
    this.builder = builder;
  }

  public Expression build(FunctionContext function) {
    return build(function.pipe());
  }

  private Expression build(PipeContext pipe) {
    Expression result = build(pipe.expression());
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

  private Expression build(ExpressionContext expression) {
    if (expression.call() != null) {
      return build(expression.call());
    }
    return buildStringExpression(expression.STRING());
  }

  private Expression build(CallContext call) {
    List<Argument> arguments = build(call.argList());
    return build(call, arguments);
  }

  private Expression build(CallContext call, List<Argument> args) {
    // UndefinedFunctionDetector has been run already so we can be sure at this
    // point that function with given name exists so getFunction won't return
    // null.
    Function function = symbolTable.getFunction(call.functionName().getText());

    Map<String, Expression> explicitArgs = builder.convert(args, function.params());

    if (explicitArgs == null) {
      return new InvalidExpression(function.type());
    } else {
      return function.apply(expressionIdFactory, explicitArgs);
    }
  }

  private List<Argument> build(ArgListContext argList) {
    List<Argument> result = Lists.newArrayList();
    if (argList != null) {
      for (ArgContext arg : argList.arg()) {
        Expression expression = build(arg.expression());
        result.add(new Argument(argName(arg), expression, argLocation(arg)));
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

  private Expression buildStringExpression(TerminalNode stringToken) {
    String quotedString = stringToken.getText();
    String string = quotedString.substring(1, quotedString.length() - 1);
    ExpressionId id = expressionIdFactory.createId(string);
    return stringExpression(id, string);
  }
}
