package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.def.Argument.namedArgument;
import static org.smoothbuild.lang.function.def.Argument.namelessArgument;
import static org.smoothbuild.lang.function.def.Argument.pipedArgument;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.basicTypes;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.ArrayContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ImplicitConverter;
import org.smoothbuild.lang.expr.InvalidExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.Argument;
import org.smoothbuild.lang.function.def.ArgumentExpressionCreator;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.UnescapingFailedException;

public class DefinedFunctionsCreator {
  private final Functions functions;
  private final ArgumentExpressionCreator argumentExpressionCreator;
  private final ImplicitConverter implicitConverter;

  @Inject
  public DefinedFunctionsCreator(Functions functions,
      ArgumentExpressionCreator argumentExpressionCreator, ImplicitConverter implicitConverter) {
    this.functions = functions;
    this.argumentExpressionCreator = argumentExpressionCreator;
    this.implicitConverter = implicitConverter;
  }

  public void createDefinedFunctions(Console console, Map<Name, FunctionContext> functionContexts,
      List<Name> sorted) {
    Worker worker = new Worker(console, functions, argumentExpressionCreator, implicitConverter);
    for (Name name : sorted) {
      functions.add(worker.build(functionContexts.get(name)));
    }
    if (console.isErrorReported()) {
      throw new ParsingException();
    }
  }

  private static class Worker {
    private final Console console;
    private final Functions functions;
    private final ArgumentExpressionCreator argumentExpressionCreator;
    private final ImplicitConverter implicitConverter;

    public Worker(Console console, Functions functions,
        ArgumentExpressionCreator argumentExpressionCreator, ImplicitConverter implicitConverter) {
      this.console = console;
      this.functions = functions;
      this.argumentExpressionCreator = argumentExpressionCreator;
      this.implicitConverter = implicitConverter;
    }

    public DefinedFunction build(FunctionContext functionContext) {
      return toFunction(functionContext);
    }

    private DefinedFunction toFunction(FunctionContext functionContext) {
      Expression expression = toExpression(functionContext.pipe());
      Name name = name(functionContext.functionName().getText());
      Signature signature = new Signature(expression.type(), name, asList());
      return new DefinedFunction(signature, expression);
    }

    private Expression toExpression(PipeContext pipeContext) {
      Expression result = toExpression(pipeContext.expression());
      List<CallContext> calls = pipeContext.call();
      for (int i = 0; i < calls.size(); i++) {
        CallContext call = calls.get(i);
        List<Argument> arguments = toArguments(call.argList());
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipeContext.p.get(i));
        arguments.add(pipedArgument(result, codeLocation));
        result = toExpression(call, arguments);
      }
      return result;
    }

    private List<Expression> toExpression(List<ExpressionContext> expressionContexts) {
      return expressionContexts.stream().map(this::toExpression).collect(toList());
    }

    private Expression toExpression(ExpressionContext expressionContext) {
      if (expressionContext.array() != null) {
        return toExpression(expressionContext.array());
      }
      if (expressionContext.call() != null) {
        return toExpression(expressionContext.call());
      }
      if (expressionContext.STRING() != null) {
        return toStringExpression(expressionContext.STRING());
      }
      throw new RuntimeException("Illegal parse tree: " + ExpressionContext.class.getSimpleName()
          + " without children.");
    }

    private Expression toExpression(ArrayContext array) {
      List<ExpressionContext> elems = array.expression();
      List<Expression> expressions = toExpression(elems);

      CodeLocation location = locationOf(array);
      Type elemType = commonSuperType(expressions, location);

      if (elemType != null) {
        return toArrayExpression(elemType, expressions, location);
      } else {
        return new InvalidExpression(NIL, location);
      }
    }

    private <T extends Value> Expression toArrayExpression(Type elemType,
        List<Expression> expressions, CodeLocation location) {
      ArrayType arrayType = Types.arrayTypeContaining(elemType);
      if (arrayType == null) {
        console.error(location, "Array cannot contain element with type " + elemType
            + ". Only following types are allowed: " + basicTypes() + ".");
        throw new ParsingException();
      }
      return new ArrayExpression(arrayType, toConvertedExpressions(elemType, expressions),
          location);
    }

    public <T extends Value> List<Expression> toConvertedExpressions(Type type,
        List<Expression> expressions) {
      return expressions.stream().map((expression) -> implicitConverter.apply(type, expression))
          .collect(toList());
    }

    private Type commonSuperType(List<Expression> expressions, CodeLocation location) {
      if (expressions.size() == 0) {
        return NOTHING;
      }
      Type firstType = expressions.get(0).type();
      Type superType = firstType;

      for (int i = 1; i < expressions.size(); i++) {
        Type type = expressions.get(i).type();
        superType = commonSuperType(superType, type);

        if (superType == null) {
          console.error(location,
              "Array cannot contain elements of incompatible types.\n"
                  + "First element has type " + firstType + " while element at index " + i
                  + " has type " + type + ".");
          throw new ParsingException();
        }
      }
      return superType;
    }

    private static Type commonSuperType(Type type1, Type type2) {
      if (type1 == STRING) {
        if (type2 == STRING) {
          return STRING;
        } else {
          return null;
        }
      } else if (type1 == BLOB) {
        if (type2 == BLOB || type2 == FILE) {
          return BLOB;
        } else {
          return null;
        }
      } else if (type1 == FILE) {
        if (type2 == FILE) {
          return FILE;
        } else if (type2 == BLOB) {
          return BLOB;
        } else {
          return null;
        }
      }
      return null;
    }

    private Expression toExpression(CallContext callContext) {
      return toExpression(callContext, toArguments(callContext.argList()));
    }

    private Expression toExpression(CallContext callContext, List<Argument> arguments) {
      FunctionNameContext functionNameContext = callContext.functionName();
      Function function = functions.get(name(functionNameContext.getText()));
      CodeLocation codeLocation = locationOf(functionNameContext);
      List<Expression> argumentExpressions = argumentExpressionCreator.createArgExprs(codeLocation,
          console, function, arguments);

      if (argumentExpressions == null) {
        return new InvalidExpression(function.type(), codeLocation);
      } else {
        return function.createCallExpression(argumentExpressions, false, codeLocation);
      }
    }

    private List<Argument> toArguments(ArgListContext argListContext) {
      List<Argument> result = new ArrayList<>();
      if (argListContext != null) {
        List<ArgContext> argContexts = argListContext.arg();
        for (int i = 0; i < argContexts.size(); i++) {
          result.add(toArgument(i, argContexts.get(i)));
        }
      }
      return result;
    }

    private Argument toArgument(int index, ArgContext arg) {
      Expression expression = toExpression(arg.expression());

      CodeLocation location = locationOf(arg);
      ParamNameContext paramName = arg.paramName();
      if (paramName == null) {
        return namelessArgument(index + 1, expression, location);
      } else {
        return namedArgument(index + 1, paramName.getText(), expression, location);
      }
    }

    private Expression toStringExpression(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      CodeLocation location = locationOf(stringToken.getSymbol());
      try {
        return new StringLiteralExpression(unescaped(string), location);
      } catch (UnescapingFailedException e) {
        console.error(location, e.getMessage());
        return new InvalidExpression(STRING, location);
      }
    }
  }
}
