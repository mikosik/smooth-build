package org.smoothbuild.lang.function.def;

import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.function.base.Parameters.parametersToNames;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.allTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ImplicitConverter;
import org.smoothbuild.lang.expr.ValueExpression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

public class ArgumentExpressionCreator {
  private final ValuesDb valuesDb;
  private final ImplicitConverter implicitConverter;

  @Inject
  public ArgumentExpressionCreator(ValuesDb valuesDb, ImplicitConverter implicitConverter) {
    this.valuesDb = valuesDb;
    this.implicitConverter = implicitConverter;
  }

  public List<Expression> createArgExprs(CodeLocation codeLocation,
      Console console, Function function, Collection<Argument> arguments) {
    ParametersPool parametersPool = new ParametersPool(function.parameters());
    ImmutableList<Argument> namedArguments = Argument.filterNamed(arguments);

    detectDuplicatedAndUnknownArgumentNames(function, console, namedArguments);
    if (console.isErrorReported()) {
      return null;
    }

    Map<Parameter, Argument> argumentMap = new HashMap<>();
    processNamedArguments(parametersPool, console, argumentMap, namedArguments);
    if (console.isErrorReported()) {
      return null;
    }

    processNamelessArguments(function, arguments, parametersPool, console, argumentMap,
        codeLocation);
    if (console.isErrorReported()) {
      return null;
    }
    Set<Parameter> missingRequiredParameters = parametersPool.allRequired();
    if (missingRequiredParameters.size() != 0) {
      console.error(codeLocation, missingRequiredArgsMessage(function, argumentMap,
          missingRequiredParameters));
      return null;
    }

    Map<String, Expression> argumentExpressions = convert(argumentMap);
    for (Parameter parameter : parametersPool.allOptional()) {
      Value value = parameter.type().defaultValue(valuesDb);
      if (value == null) {
        console.error(codeLocation, "Parameter '" + parameter.name()
            + "' has to be assigned explicitly as type '" + parameter.type().name()
            + "' doesn't have default value.");
      } else {
        Expression expression = new ValueExpression(value, codeLocation);
        argumentExpressions.put(parameter.name(), expression);
      }
    }
    if (console.isErrorReported()) {
      return null;
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

  private List<Expression> sortAccordingToParametersOrder(
      Map<String, Expression> argumentExpressions, Function function) {
    ImmutableList.Builder<Expression> builder = ImmutableList.builder();
    for (Parameter parameter : function.parameters()) {
      builder.add(argumentExpressions.get(parameter.name()));
    }
    return builder.build();
  }

  private static void detectDuplicatedAndUnknownArgumentNames(Function function,
      Console console, Collection<Argument> namedArguments) {
    Set<String> unusedNames = new HashSet<>(parametersToNames(function.parameters()));
    Set<String> usedNames = new HashSet<>();
    for (Argument argument : namedArguments) {
      if (argument.hasName()) {
        String name = argument.name();
        if (unusedNames.contains(name)) {
          unusedNames.remove(name);
          usedNames.add(name);
        } else if (usedNames.contains(name)) {
          console.error(argument.codeLocation(), "Argument '" + argument.name()
              + "' assigned twice.");
        } else {
          console.error(argument.codeLocation(), "Function " + function.name()
              + " has no parameter '" + argument.name() + "'.");
        }
      }
    }
  }

  private static void processNamedArguments(ParametersPool parametersPool, Console console,
      Map<Parameter, Argument> argumentMap, Collection<Argument> namedArguments) {
    for (Argument argument : namedArguments) {
      if (argument.hasName()) {
        String name = argument.name();
        Parameter parameter = parametersPool.take(name);
        Type paramType = parameter.type();
        if (!canConvert(argument.type(), paramType)) {
          console.error(argument.codeLocation(), "Type mismatch, cannot convert argument '"
              + argument.name() + "' of type '" + argument.type().name() + "' to '" + paramType
                  .name() + "'.");
        } else {
          argumentMap.put(parameter, argument);
        }
      }
    }
  }

  private static void processNamelessArguments(Function function, Collection<Argument> arguments,
      ParametersPool parametersPool, Console console, Map<Parameter, Argument> argumentMap,
      CodeLocation codeLocation) {
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
          console.error(codeLocation, ambiguousAssignmentErrorMessage(function, argumentMap,
              availableArguments, availableTypedParams));
          return;
        }
      }
    }
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
      Expression expression = implicitConverter.apply(parameter.type(), argument.expression());
      map.put(parameter.name(), expression);
    }

    return map;
  }
}
