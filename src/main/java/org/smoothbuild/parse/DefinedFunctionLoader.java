package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.allTypes;
import static org.smoothbuild.lang.type.Types.commonSuperType;
import static org.smoothbuild.parse.arg.Argument.argument;
import static org.smoothbuild.util.Lists.map;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.antlr.SmoothParser.ExprContext;
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
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.util.Maybe;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

public class DefinedFunctionLoader {

  public static Maybe<DefinedFunction> loadDefinedFunction(Functions loadedFunctions,
      FuncNode funcNode) {
    return new Worker(loadedFunctions).loadFunction(funcNode);
  }

  private static class Worker {
    private final Functions loadedFunctions;

    public Worker(Functions loadedFunctions) {
      this.loadedFunctions = loadedFunctions;
    }

    public Maybe<DefinedFunction> loadFunction(FuncNode node) {
      List<Parameter> parameters = createParameters(node.params());
      Maybe<Expression> expression = createExpression(node.expr());
      return invokeWrap(expression, e -> createFunction(node, e));
    }

    private static List<Parameter> createParameters(List<ParamNode> params) {
      return params
          .stream()
          .map(p -> parameter(p.type().get(Type.class), p.name()))
          .collect(toList());
    }

    private static DefinedFunction createFunction(FuncNode node, Expression expression) {
      Signature signature = new Signature(expression.type(), node.name(), asList());
      return new DefinedFunction(signature, expression);
    }

    private Maybe<Expression> createExpression(ExprNode node) {
      if (node instanceof CallNode) {
        return createCall((CallNode) node);
      }
      if (node instanceof StringNode) {
        return createStringLiteral((StringNode) node);
      }
      if (node instanceof ArrayNode) {
        return createArray((ArrayNode) node);
      }
      throw new RuntimeException("Illegal parse tree: " + ExprContext.class.getSimpleName()
          + " without children.");
    }

    private Maybe<Expression> createArray(ArrayNode node) {
      Maybe<List<Expression>> exprList = pullUp(map(node.elements(), this::createExpression));
      CodeLocation location = node.codeLocation();
      Maybe<ArrayType> arrayType = invoke(exprList, es -> arrayType(es, location));
      return invokeWrap(arrayType, at -> createArray(at, exprList.value(), location));
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
      ArrayType arrayType = Types.arrayOf(superType);
      if (arrayType == null) {
        return error(new ParseError(location, "Array cannot contain element with type "
            + superType + ". Only following types are allowed: " + Types.basicTypes()
            + "."));
      }

      return value(arrayType);
    }

    private Maybe<Expression> createCall(CallNode node) {
      Maybe<List<Argument>> args = convertArgNodesToArguments(node.args());
      Function function = loadedFunctions.get(node.name());
      Maybe<List<Expression>> expressions = invoke(args,
          a -> createArgExprs(node.codeLocation(), function, a));
      return invokeWrap(expressions,
          es -> function.createCallExpression(es, false, node.codeLocation()));
    }

    private Maybe<List<Argument>> convertArgNodesToArguments(List<ArgNode> args) {
      List<Maybe<Argument>> result = new ArrayList<Maybe<Argument>>();
      for (ArgNode argNode : args) {
        Maybe<Expression> expression = createExpression(argNode.expr());
        Maybe<Argument> argument = invokeWrap(expression,
            e -> argument(argNode.number(), argNode.name(), e, argNode.codeLocation()));
        result.add(argument);
      }
      return Maybe.pullUp(result);

    }

    private Maybe<Expression> createStringLiteral(StringNode node) {
      try {
        return value(new StringLiteralExpression(
            unescaped(node.value()), node.codeLocation()));
      } catch (UnescapingFailedException e) {
        return error(new ParseError(node.codeLocation(), e.getMessage()));
      }
    }

    public Maybe<List<Expression>> createArgExprs(CodeLocation codeLocation, Function function,
        List<Argument> arguments) {
      ParametersPool parametersPool = new ParametersPool(function.parameters());
      List<Argument> namedArguments = Argument.filterNamed(arguments);

      Map<Parameter, Argument> argumentMap = new HashMap<>();
      List<Object> errors = processNamedArguments(parametersPool, argumentMap, namedArguments);
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
