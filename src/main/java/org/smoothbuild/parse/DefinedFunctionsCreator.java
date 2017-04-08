package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.parse.Parsed.invoke;
import static org.smoothbuild.parse.Parsed.parsed;
import static org.smoothbuild.parse.arg.Argument.namedArgument;
import static org.smoothbuild.parse.arg.Argument.namelessArgument;
import static org.smoothbuild.parse.arg.Argument.pipedArgument;
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
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.arg.Argument;
import org.smoothbuild.parse.arg.ArgumentExpressionCreator;
import org.smoothbuild.util.Lists;
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
    new Worker(console, functions, argumentExpressionCreator, implicitConverter)
        .functionList(functionContexts, sorted);
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

    public void functionList(Map<Name, FunctionContext> functionContexts, List<Name> sorted) {
      for (Name name : sorted) {
        Parsed<DefinedFunction> function = parseFunction(functionContexts.get(name));
        if (function.hasResult()) {
          functions.add(function.result());
        }
        for (String error : function.errors()) {
          console.rawError(error);
        }
      }
      if (console.isErrorReported()) {
        throw new ParsingException();
      }
    }

    private Parsed<DefinedFunction> parseFunction(FunctionContext functionContext) {
      Parsed<Expression> expression = parsePipe(functionContext.pipe());
      Name name = name(functionContext.functionName().getText());
      return invoke(expression, (expression_) -> {
        Signature signature = new Signature(expression_.type(), name, asList());
        return new DefinedFunction(signature, expression_);
      });
    }

    private Parsed<Expression> parsePipe(PipeContext pipeContext) {
      Parsed<Expression> result = parseExpression(pipeContext.expression());
      List<CallContext> calls = pipeContext.call();
      for (int i = 0; i < calls.size(); i++) {
        CallContext call = calls.get(i);
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipeContext.p.get(i));
        Parsed<Argument> pipedArgument = invoke(result, result_ -> {
          return pipedArgument(result_, codeLocation);
        });
        Parsed<List<Argument>> arguments = parseArgumentList(call.argList());
        arguments = invoke(arguments, pipedArgument, Lists::concat);
        result = parseCall(call, arguments);
      }
      return result;
    }

    private Parsed<List<Expression>> parseExpressionList(
        List<ExpressionContext> expressionContexts) {
      Parsed<List<Expression>> result = parsed(new ArrayList<>());
      for (ExpressionContext expressionContext : expressionContexts) {
        result = invoke(result, parseExpression(expressionContext), Lists::concat);
      }
      return result;
    }

    private Parsed<Expression> parseExpression(ExpressionContext expressionContext) {
      if (expressionContext.array() != null) {
        return parseArray(expressionContext.array());
      }
      if (expressionContext.call() != null) {
        return parseCall(expressionContext.call());
      }
      if (expressionContext.STRING() != null) {
        return parseStringLiteral(expressionContext.STRING());
      }
      throw new RuntimeException("Illegal parse tree: " + ExpressionContext.class.getSimpleName()
          + " without children.");
    }

    private Parsed<Expression> parseArray(ArrayContext array) {
      List<ExpressionContext> elems = array.expression();
      Parsed<List<Expression>> expressions = parseExpressionList(elems);
      CodeLocation location = locationOf(array);
      Parsed<Type> elemType = commonSuperType(expressions, location);
      return invoke(elemType, expressions, (elemType_, expressions_) -> {
        ArrayType arrayType = Types.arrayTypeContaining(elemType_);
        if (arrayType == null) {
          console.error(location, "Array cannot contain element with type " + elemType.result()
              + ". Only following types are allowed: " + Types.basicTypes() + ".");
          throw new ParsingException();
        }
        List<Expression> converted = expressions_.stream()
            .map((expression) -> implicitConverter.apply(elemType_, expression))
            .collect(toList());
        return new ArrayExpression(arrayType, converted, location);
      });
    }

    private Parsed<Type> commonSuperType(Parsed<List<Expression>> expressions,
        CodeLocation location) {
      if (!expressions.hasResult()) {
        return invoke(expressions, expressions_ -> null);
      }
      List<Expression> list = expressions.result();
      if (list.isEmpty()) {
        return parsed(NOTHING);
      }
      Type firstType = list.get(0).type();
      Type superType = firstType;

      for (int i = 1; i < list.size(); i++) {
        Type type = list.get(i).type();
        superType = commonSuperType(superType, type);

        if (superType == null) {
          return Parsed.error(location,
              "Array cannot contain elements of incompatible types.\n"
                  + "First element has type " + firstType + " while element at index " + i
                  + " has type " + type + ".");
        }
      }
      return parsed(superType);
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

    private Parsed<Expression> parseCall(CallContext callContext) {
      return parseCall(callContext, parseArgumentList(callContext.argList()));
    }

    private Parsed<Expression> parseCall(CallContext callContext,
        Parsed<List<Argument>> arguments) {
      FunctionNameContext functionNameContext = callContext.functionName();
      Function function = functions.get(name(functionNameContext.getText()));
      CodeLocation codeLocation = locationOf(functionNameContext);
      Parsed<List<Expression>> argumentExpressions =
          invoke(arguments, arguments_ -> {
            return argumentExpressionCreator.createArgExprs(
                codeLocation, console, function, arguments_);
          });
      if (argumentExpressions.hasResult()) {
        return parsed(function.createCallExpression(argumentExpressions.result(), false,
            codeLocation));
      } else {
        return parsed((Expression) new InvalidExpression(function.type(), codeLocation))
            .addErrors(argumentExpressions.errors());
      }
    }

    private Parsed<List<Argument>> parseArgumentList(ArgListContext argListContext) {
      Parsed<List<Argument>> result = parsed(new ArrayList<>());
      if (argListContext != null) {
        List<ArgContext> argContexts = argListContext.arg();
        for (int i = 0; i < argContexts.size(); i++) {
          result = invoke(result, parseArgument(i, argContexts.get(i)), Lists::concat);
        }
      }
      return result;
    }

    private Parsed<Argument> parseArgument(int index, ArgContext arg) {
      Parsed<Expression> expression = parseExpression(arg.expression());
      return invoke(expression, expression_ -> {
        CodeLocation location = locationOf(arg);
        ParamNameContext paramName = arg.paramName();
        if (paramName == null) {
          return namelessArgument(index + 1, expression_, location);
        } else {
          return namedArgument(index + 1, paramName.getText(), expression_, location);
        }
      });
    }

    private Parsed<Expression> parseStringLiteral(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      CodeLocation location = locationOf(stringToken.getSymbol());
      try {
        return parsed(new StringLiteralExpression(unescaped(string), location));
      } catch (UnescapingFailedException e) {
        return parsed((Expression) new InvalidExpression(STRING, location))
            .addError(location, e.getMessage());
      }
    }
  }
}
