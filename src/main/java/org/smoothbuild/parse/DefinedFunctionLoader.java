package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.function.base.Parameters.parametersToNames;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.allTypes;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.parse.Maybe.invokeWrap;
import static org.smoothbuild.parse.Maybe.result;
import static org.smoothbuild.parse.arg.Argument.namedArgument;
import static org.smoothbuild.parse.arg.Argument.namelessArgument;
import static org.smoothbuild.parse.arg.Argument.pipedArgument;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.DefaultValueExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.InvalidExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Conversions;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.parse.arg.Argument;
import org.smoothbuild.parse.arg.MapToString;
import org.smoothbuild.parse.arg.ParametersPool;
import org.smoothbuild.parse.arg.TypedParametersPool;
import org.smoothbuild.util.Lists;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

public class DefinedFunctionLoader {

  public static Maybe<DefinedFunction> loadDefinedFunction(Functions loadedFunctions,
      FunctionContext functionContext) {
    return new Worker(loadedFunctions).loadFunction(functionContext);
  }

  private static class Worker {
    private final Functions loadedFunctions;

    public Worker(Functions loadedFunctions) {
      this.loadedFunctions = loadedFunctions;
    }

    public Maybe<DefinedFunction> loadFunction(FunctionContext functionContext) {
      Maybe<Expression> expression = parsePipe(functionContext.pipe());
      Name name = name(functionContext.functionName().getText());
      return invokeWrap(expression, (expression_) -> {
        Signature signature = new Signature(expression_.type(), name, asList());
        return new DefinedFunction(signature, expression_);
      });
    }

    private Maybe<Expression> parsePipe(PipeContext pipeContext) {
      Maybe<Expression> result = parseExpression(pipeContext.expression());
      List<CallContext> calls = pipeContext.call();
      for (int i = 0; i < calls.size(); i++) {
        CallContext call = calls.get(i);
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipeContext.p.get(i));
        Maybe<Argument> pipedArgument = invokeWrap(result, result_ -> {
          return pipedArgument(result_, codeLocation);
        });
        Maybe<List<Argument>> arguments = parseArgumentList(call.argList());
        arguments = invokeWrap(arguments, pipedArgument, Lists::concat);
        if (arguments.hasResult()) {
          result = parseCall(call, arguments.result());
        } else {
          Maybe.errors(arguments.errors());
        }
      }
      return result;
    }

    private Maybe<List<Expression>> parseExpressionList(
        List<ExpressionContext> expressionContexts) {
      Maybe<List<Expression>> result = result(new ArrayList<>());
      for (ExpressionContext expressionContext : expressionContexts) {
        result = invokeWrap(result, parseExpression(expressionContext), Lists::concat);
      }
      return result;
    }

    private Maybe<Expression> parseExpression(ExpressionContext expressionContext) {
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

    private Maybe<Expression> parseArray(ArrayContext array) {
      List<ExpressionContext> elems = array.expression();
      Maybe<List<Expression>> expressions = parseExpressionList(elems);
      CodeLocation location = locationOf(array);
      Maybe<Type> elemType = commonSuperType(expressions, location);
      if (!(expressions.hasResult() && elemType.hasResult())) {
        return Maybe.<Expression> errors(expressions.addErrors(elemType.errors()).errors());
      }
      List<Expression> pureExpressions = expressions.result();
      Type pureType = elemType.result();
      ArrayType arrayType = Types.arrayTypeContaining(pureType);
      if (arrayType == null) {
        return Maybe.error(location, "Array cannot contain element with type " + elemType.result()
            + ". Only following types are allowed: " + Types.basicTypes() + ".");
      }

      List<Expression> converted = pureExpressions.stream()
          .map((e) -> implicitConversion(pureType, e))
          .collect(toList());
      return result(new ArrayExpression(arrayType, converted, location));
    }

    private Maybe<Type> commonSuperType(Maybe<List<Expression>> expressions,
        CodeLocation location) {
      if (!expressions.hasResult()) {
        return invokeWrap(expressions, expressions_ -> null);
      }
      List<Expression> list = expressions.result();
      if (list.isEmpty()) {
        return result(NOTHING);
      }
      Type firstType = list.get(0).type();
      Type superType = firstType;

      for (int i = 1; i < list.size(); i++) {
        Type type = list.get(i).type();
        superType = commonSuperType(superType, type);

        if (superType == null) {
          return Maybe.error(location,
              "Array cannot contain elements of incompatible types.\n"
                  + "First element has type " + firstType + " while element at index " + i
                  + " has type " + type + ".");
        }
      }
      return result(superType);
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

    private Maybe<Expression> parseCall(CallContext callContext) {
      Maybe<List<Argument>> argumentList = parseArgumentList(callContext.argList());
      if (argumentList.hasResult()) {
        return parseCall(callContext, argumentList.result());
      } else {
        return Maybe.errors(argumentList.errors());
      }
    }

    private Maybe<Expression> parseCall(CallContext callContext, List<Argument> arguments) {
      FunctionNameContext functionNameContext = callContext.functionName();
      Function function = loadedFunctions.get(name(functionNameContext.getText()));
      CodeLocation codeLocation = locationOf(functionNameContext);
      Maybe<List<Expression>> argumentExpressions = createArgExprs(codeLocation, function,
          arguments);
      if (argumentExpressions.hasResult()) {
        return result(function.createCallExpression(argumentExpressions.result(), false,
            codeLocation));
      } else {
        return result((Expression) new InvalidExpression(function.type(), codeLocation))
            .addErrors(argumentExpressions.errors());
      }
    }

    private Maybe<List<Argument>> parseArgumentList(ArgListContext argListContext) {
      Maybe<List<Argument>> result = result(new ArrayList<>());
      if (argListContext != null) {
        List<ArgContext> argContexts = argListContext.arg();
        for (int i = 0; i < argContexts.size(); i++) {
          Maybe<Argument> argument = parseArgument(i, argContexts.get(i));
          result = invokeWrap(result, argument, Lists::concat);
        }
      }
      return result;
    }

    private Maybe<Argument> parseArgument(int index, ArgContext arg) {
      Maybe<Expression> expression = parseExpression(arg.expression());
      return invokeWrap(expression, expression_ -> {
        CodeLocation location = locationOf(arg);
        ParamNameContext paramName = arg.paramName();
        if (paramName == null) {
          return namelessArgument(index + 1, expression_, location);
        } else {
          return namedArgument(index + 1, paramName.getText(), expression_, location);
        }
      });
    }

    private Maybe<Expression> parseStringLiteral(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      CodeLocation location = locationOf(stringToken.getSymbol());
      try {
        return result(new StringLiteralExpression(unescaped(string), location));
      } catch (UnescapingFailedException e) {
        return result((Expression) new InvalidExpression(STRING, location))
            .addError(location, e.getMessage());
      }
    }

    public Maybe<List<Expression>> createArgExprs(CodeLocation codeLocation, Function function,
        List<Argument> arguments) {
      ParametersPool parametersPool = new ParametersPool(function.parameters());
      List<Argument> namedArguments = Argument.filterNamed(arguments);

      List<Object> errors = duplicatedAndUnknownArgumentNames(function, namedArguments);
      if (!errors.isEmpty()) {
        return Maybe.errors(errors);
      }

      Map<Parameter, Argument> argumentMap = new HashMap<>();
      errors = processNamedArguments(parametersPool, argumentMap, namedArguments);
      if (!errors.isEmpty()) {
        return Maybe.errors(errors);
      }

      errors = processNamelessArguments(function, arguments, parametersPool, argumentMap,
          codeLocation);
      if (!errors.isEmpty()) {
        return Maybe.errors(errors);
      }
      Set<Parameter> missingRequiredParameters = parametersPool.allRequired();
      if (missingRequiredParameters.size() != 0) {
        return Maybe.error(codeLocation, missingRequiredArgsMessage(function, argumentMap,
            missingRequiredParameters));
      }

      Map<String, Expression> argumentExpressions = convert(argumentMap);
      for (Parameter parameter : parametersPool.allOptional()) {
        if (parameter.type() == Types.NOTHING) {
          return Maybe.error(codeLocation, "Parameter '" + parameter.name() + "' has to be "
              + "assigned explicitly as type 'Nothing' doesn't have default value.");
        } else {
          Expression expression = new DefaultValueExpression(parameter.type(), codeLocation);
          argumentExpressions.put(parameter.name(), expression);
        }
      }

      return sortAccordingToParametersOrder(argumentExpressions, function);
    }

    private static String missingRequiredArgsMessage(Function function,
        Map<Parameter, Argument> argumentMap, Set<Parameter> missingRequiredParameters) {
      return "Not all parameters required by " + function.name() + " function has been specified.\n"
          + "Missing required parameters:\n"
          + parametersToString(missingRequiredParameters)
          + "All correct 'parameters <- arguments' assignments:\n"
          + argumentMap.toString();
    }

    private Maybe<List<Expression>> sortAccordingToParametersOrder(
        Map<String, Expression> argumentExpressions, Function function) {
      ImmutableList.Builder<Expression> builder = ImmutableList.builder();
      for (Parameter parameter : function.parameters()) {
        builder.add(argumentExpressions.get(parameter.name()));
      }
      return result(builder.build());
    }

    private static List<Object> duplicatedAndUnknownArgumentNames(Function function,
        Collection<Argument> namedArguments) {
      ArrayList<Object> errors = new ArrayList<>();
      Set<String> unusedNames = new HashSet<>(parametersToNames(function.parameters()));
      Set<String> usedNames = new HashSet<>();
      for (Argument argument : namedArguments) {
        if (argument.hasName()) {
          String name = argument.name();
          if (unusedNames.contains(name)) {
            unusedNames.remove(name);
            usedNames.add(name);
          } else if (usedNames.contains(name)) {
            errors.add(new ParseError(argument.codeLocation(), "Argument '" + argument.name()
                + "' assigned twice."));
          } else {
            errors.add(new ParseError(argument.codeLocation(), "Function " + function.name()
                + " has no parameter '" + argument.name() + "'."));
          }
        }
      }
      return errors;
    }

    private static List<Object> processNamedArguments(ParametersPool parametersPool,
        Map<Parameter, Argument> argumentMap, Collection<Argument> namedArguments) {
      ArrayList<Object> errors = new ArrayList<>();
      for (Argument argument : namedArguments) {
        if (argument.hasName()) {
          String name = argument.name();
          Parameter parameter = parametersPool.take(name);
          Type paramType = parameter.type();
          if (!canConvert(argument.type(), paramType)) {
            errors.add(new ParseError(argument.codeLocation(),
                "Type mismatch, cannot convert argument '" + argument.name() + "' of type '"
                    + argument.type().name() + "' to '" + paramType.name() + "'."));
          } else {
            argumentMap.put(parameter, argument);
          }
        }
      }
      return errors;
    }

    private static List<Object> processNamelessArguments(Function function,
        Collection<Argument> arguments, ParametersPool parametersPool,
        Map<Parameter, Argument> argumentMap, CodeLocation codeLocation) {
      ImmutableMultimap<Type, Argument> namelessArgs = Argument.filterNameless(arguments);
      for (Type type : allTypes()) {
        Collection<Argument> availableArguments = namelessArgs.get(type);
        int argsSize = availableArguments.size();
        if (0 < argsSize) {
          TypedParametersPool availableTypedParams = parametersPool.assignableFrom(type);

          if (argsSize == 1 && availableTypedParams.hasCandidate()) {
            Argument onlyArgument = availableArguments.iterator().next();
            Parameter candidateParameter = availableTypedParams.candidate();
            argumentMap.put(candidateParameter, onlyArgument);
            parametersPool.take(candidateParameter);
          } else {
            String message = ambiguousAssignmentErrorMessage(function, argumentMap,
                availableArguments, availableTypedParams);
            return asList(new ParseError(codeLocation, message));
          }
        }
      }
      return new ArrayList<>();
    }

    private static String ambiguousAssignmentErrorMessage(Function function,
        Map<Parameter, Argument> argumentMap, Collection<Argument> availableArguments,
        TypedParametersPool availableTypedParams) {
      String assignmentList = MapToString.toString(argumentMap);
      if (availableTypedParams.isEmpty()) {
        return "Can't find parameter(s) of proper type in "
            + function.name()
            + " function for some nameless argument(s):\n"
            + "List of assignments that were successfully detected so far is following:\n"
            + assignmentList
            + "List of arguments for which no parameter could be found is following:\n"
            + argsToList(availableArguments);
      } else {
        return "Can't decide unambiguously to which parameters in " + function.name()
            + " function some nameless arguments should be assigned:\n"
            + "List of assignments that were successfully detected is following:\n"
            + assignmentList
            + "List of nameless arguments that caused problems:\n"
            + argsToList(availableArguments)
            + "List of unassigned parameters of desired type is following:\n"
            + availableTypedParams.toFormattedString();
      }
    }

    private static String argsToList(Collection<Argument> availableArguments) {
      List<Argument> arguments = Argument.NUMBER_ORDERING.sortedCopy(availableArguments);
      int typeLength = longestArgType(arguments);
      int nameLength = longestArgName(arguments);
      int numberLength = longestArgNumber(arguments);

      StringBuilder builder = new StringBuilder();
      for (Argument argument : arguments) {
        builder.append("  " + argument.toPaddedString(typeLength, nameLength, numberLength) + "\n");
      }
      return builder.toString();
    }

    private static int longestArgType(List<Argument> arguments) {
      int result = 0;
      for (Argument argument : arguments) {
        result = Math.max(result, argument.type().name().length());
      }
      return result;
    }

    private static int longestArgName(List<Argument> arguments) {
      int result = 0;
      for (Argument argument : arguments) {
        result = Math.max(result, argument.nameSanitized().length());
      }
      return result;
    }

    private static int longestArgNumber(List<Argument> arguments) {
      int maxNumber = 0;
      for (Argument argument : arguments) {
        maxNumber = Math.max(maxNumber, argument.number());
      }
      return Integer.toString(maxNumber).length();
    }

    private Map<String, Expression> convert(Map<Parameter, Argument> paramToArgMap) {
      Map<String, Expression> map = new HashMap<>();

      for (Map.Entry<Parameter, Argument> entry : paramToArgMap.entrySet()) {
        Parameter parameter = entry.getKey();
        Argument argument = entry.getValue();
        Expression expression = implicitConversion(parameter.type(), argument.expression());
        map.put(parameter.name(), expression);
      }

      return map;
    }

    public <T extends Value> Expression implicitConversion(Type destinationType,
        Expression source) {
      Type sourceType = source.type();
      if (sourceType == destinationType) {
        return source;
      }

      Name functionName = Conversions.convertFunctionName(sourceType, destinationType);
      Function function = loadedFunctions.get(functionName);
      return function.createCallExpression(asList(source), true, source.codeLocation());
    }
  }
}
