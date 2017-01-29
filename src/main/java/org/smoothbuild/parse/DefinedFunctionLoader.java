package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.function.base.Parameters.parametersToNames;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.allTypes;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.parse.arg.Argument.namedArgument;
import static org.smoothbuild.parse.arg.Argument.namelessArgument;
import static org.smoothbuild.parse.arg.Argument.pipedArgument;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.errors;
import static org.smoothbuild.util.Maybe.invoke;
import static org.smoothbuild.util.Maybe.invokeWrap;
import static org.smoothbuild.util.Maybe.pullUp;
import static org.smoothbuild.util.Maybe.value;
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
import org.smoothbuild.antlr.SmoothParser.ArrayTypeContext;
import org.smoothbuild.antlr.SmoothParser.BasicTypeContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.antlr.SmoothParser.ParamContext;
import org.smoothbuild.antlr.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.antlr.SmoothParser.TypeContext;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.DefaultValueExpression;
import org.smoothbuild.lang.expr.Expression;
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
import org.smoothbuild.util.Maybe;
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
      Maybe<List<Parameter>> parameters = parseParameters(functionContext.paramList());
      Maybe<Expression> expression = parsePipe(functionContext.pipe());
      return invokeWrap(parameters, expression, (p, e) -> createFunction(functionContext, e));
    }

    private Maybe<List<Parameter>> parseParameters(ParamListContext context) {
      List<Maybe<Parameter>> parameters = new ArrayList<>();
      List<ParamContext> contexts = context == null ? asList() : sane(context.param());
      for (int i = 0; i < contexts.size(); i++) {
        parameters.add(parseParameter(i, contexts.get(i)));
      }
      Maybe<List<Parameter>> result = pullUp(parameters);
      if (result.hasValue()) {
        return result.addErrors(duplicateParameterNameErrors(result.value(), contexts));
      } else {
        return result;
      }
    }

    public ArrayList<ParseError> duplicateParameterNameErrors(List<Parameter> parameters,
        List<ParamContext> contexts) {
      ArrayList<ParseError> result = new ArrayList<>();
      Set<String> names = new HashSet<>();
      for (int i = 0; i < parameters.size(); i++) {
        String name = parameters.get(i).name();
        if (names.contains(name)) {
          result.add(new ParseError(
              locationOf(contexts.get(i)), "Duplicate parameter '" + name + "'."));
        }
        names.add(name);
      }
      return result;
    }

    private Maybe<Parameter> parseParameter(int i, ParamContext context) {
      String name = context.name().getText();
      Maybe<Type> type = parseType(context.type());
      return invokeWrap(type, t -> parameter(t, name));
    }

    private Maybe<Type> parseType(TypeContext context) {
      if (context.basicType() != null) {
        return parseBasicType(context.basicType());
      }
      if (context.arrayType() != null) {
        return parseArrayType(context.arrayType());
      }
      throw new RuntimeException("Illegal parse tree: " + TypeContext.class.getSimpleName()
          + " without children.");
    }

    private Maybe<Type> parseBasicType(BasicTypeContext context) {
      Type type = Types.basicTypeFromString(context.getText());
      if (type == null) {
        return error(
            new ParseError(locationOf(context), "Unknown type '" + context.getText() + "'."));
      }
      return value(type);
    }

    private Maybe<Type> parseArrayType(ArrayTypeContext context) {
      Maybe<Type> elementType = parseType(context.type());
      return invoke(elementType, et -> {
        if (et instanceof ArrayType) {
          return error(new ParseError(locationOf(context), "Nested array type is forbidden."));
        }
        return value(Types.arrayTypeContaining(et));
      });
    }

    private static DefinedFunction createFunction(FunctionContext functionContext,
        Expression expression) {
      Name name = name(functionContext.name().getText());
      Signature signature = new Signature(expression.type(), name, asList());
      return new DefinedFunction(signature, expression);
    }

    private Maybe<Expression> parsePipe(PipeContext pipeContext) {
      Maybe<Expression> result = parseExpression(pipeContext.expression());
      List<CallContext> calls = pipeContext.call();
      for (int i = 0; i < calls.size(); i++) {
        CallContext call = calls.get(i);
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipeContext.p.get(i));
        Maybe<Argument> pipedArg = invokeWrap(result, r -> pipedArgument(r, codeLocation));
        Maybe<List<Argument>> restArgs = parseArgumentList(call.argList());
        Maybe<List<Argument>> allArgs = invokeWrap(restArgs, pipedArg, Lists::concat);
        result = invoke(allArgs, as -> parseCall(call, as));
      }
      return result;
    }

    private Maybe<List<Expression>> parseExpressionList(List<ExpressionContext> expressions) {
      return pullUp(map(expressions, this::parseExpression));
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
      Maybe<ArrayType> arrayType = invoke(expressions, es -> arrayType(es, location));
      return invokeWrap(arrayType, at -> createArray(at, expressions.value(), location));
    }

    private Expression createArray(ArrayType type, List<Expression> elements,
        CodeLocation location) {
      List<Expression> converted = map(elements, e -> implicitConversion(type.elemType(), e));
      return new ArrayExpression(type, converted, location);
    }

    private Maybe<ArrayType> arrayType(List<Expression> expressions, CodeLocation location) {
      if (expressions.isEmpty()) {
        return value(NIL);
      }
      Type firstType = expressions.get(0).type();
      Type superType = firstType;

      for (int i = 1; i < expressions.size(); i++) {
        Type type = expressions.get(i).type();
        superType = commonSuperType(superType, type);

        if (superType == null) {
          return error(new ParseError(location,
              "Array cannot contain elements of incompatible types.\n"
                  + "First element has type " + firstType + " while element at index " + i
                  + " has type " + type + "."));
        }
      }
      ArrayType arrayType = Types.arrayTypeContaining(superType);
      if (arrayType == null) {
        return error(new ParseError(location, "Array cannot contain element with type "
            + superType + ". Only following types are allowed: " + Types.basicTypes()
            + "."));
      }

      return value(arrayType);
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
      Maybe<List<Argument>> arguments = parseArgumentList(callContext.argList());
      return invoke(arguments, as -> parseCall(callContext, as));
    }

    private Maybe<Expression> parseCall(CallContext callContext, List<Argument> arguments) {
      Function function = loadedFunctions.get(name(callContext.name().getText()));
      CodeLocation codeLocation = locationOf(callContext.name());
      Maybe<List<Expression>> expressions = createArgExprs(codeLocation, function, arguments);
      return invokeWrap(expressions, es -> function.createCallExpression(es, false, codeLocation));
    }

    private Maybe<List<Argument>> parseArgumentList(ArgListContext argListContext) {
      List<Maybe<Argument>> result = new ArrayList<>();
      if (argListContext != null) {
        List<ArgContext> argContexts = argListContext.arg();
        for (int i = 0; i < argContexts.size(); i++) {
          result.add(parseArgument(i, argContexts.get(i)));
        }
      }
      return pullUp(result);
    }

    private Maybe<Argument> parseArgument(int index, ArgContext arg) {
      Maybe<Expression> expression = parseExpression(arg.expression());
      return invokeWrap(expression, e -> createArgument(index, arg, e));
    }

    private Argument createArgument(int index, ArgContext arg, Expression expression_) {
      CodeLocation location = locationOf(arg);
      NameContext paramName = arg.name();
      if (paramName == null) {
        return namelessArgument(index + 1, expression_, location);
      } else {
        return namedArgument(index + 1, paramName.getText(), expression_, location);
      }
    }

    private Maybe<Expression> parseStringLiteral(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      CodeLocation location = locationOf(stringToken.getSymbol());
      try {
        return value(new StringLiteralExpression(unescaped(string), location));
      } catch (UnescapingFailedException e) {
        return error(new ParseError(location, e.getMessage()));
      }
    }

    public Maybe<List<Expression>> createArgExprs(CodeLocation codeLocation, Function function,
        List<Argument> arguments) {
      ParametersPool parametersPool = new ParametersPool(function.parameters());
      List<Argument> namedArguments = Argument.filterNamed(arguments);

      List<Object> errors = duplicatedAndUnknownArgumentNames(function, namedArguments);
      if (!errors.isEmpty()) {
        return errors(errors);
      }

      Map<Parameter, Argument> argumentMap = new HashMap<>();
      errors = processNamedArguments(parametersPool, argumentMap, namedArguments);
      if (!errors.isEmpty()) {
        return errors(errors);
      }

      errors = processNamelessArguments(function, arguments, parametersPool, argumentMap,
          codeLocation);
      if (!errors.isEmpty()) {
        return errors(errors);
      }
      Set<Parameter> missingRequiredParameters = parametersPool.allRequired();
      if (missingRequiredParameters.size() != 0) {
        return error(new ParseError(codeLocation,
            missingRequiredArgsMessage(function, argumentMap, missingRequiredParameters)));
      }

      Map<String, Expression> argumentExpressions = convert(argumentMap);
      for (Parameter parameter : parametersPool.allOptional()) {
        if (parameter.type() == Types.NOTHING) {
          return error(new ParseError(codeLocation, "Parameter '" + parameter.name()
              + "' has to be assigned explicitly as type 'Nothing' doesn't have default value."));
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
      return value(builder.build());
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
