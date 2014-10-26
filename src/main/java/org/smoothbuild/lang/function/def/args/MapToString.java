package org.smoothbuild.lang.function.def.args;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.util.LineBuilder;

public class MapToString {
  public static String toString(Map<Parameter, Argument> paramToArgMap) {
    int maxParamType = longestParameterType(paramToArgMap.keySet());
    int maxParamName = longestParameterName(paramToArgMap.keySet());
    int maxArgType = longestArgumentType(paramToArgMap.values());
    int maxArgName = longestArgumentName(paramToArgMap.values());
    int maxNumber = longestArgumentNumber(paramToArgMap.values());

    LineBuilder builder = new LineBuilder();
    for (Map.Entry<Parameter, Argument> entry : paramToArgMap.entrySet()) {
      String paramPart = entry.getKey().toPaddedString(maxParamType, maxParamName);
      Argument argument = entry.getValue();
      String argPart = argument.toPaddedString(maxArgType, maxArgName, maxNumber);
      builder.addLine("  " + paramPart + " <- " + argPart);
    }
    return builder.build();
  }

  private static int longestParameterType(Set<Parameter> parameters) {
    int result = 0;
    for (Parameter parameter : parameters) {
      result = Math.max(result, parameter.type().name().length());
    }
    return result;
  }

  private static int longestParameterName(Set<Parameter> parameters) {
    int result = 0;
    for (Parameter parameter : parameters) {
      result = Math.max(result, parameter.name().length());
    }
    return result;
  }

  private static int longestArgumentType(Collection<Argument> arguments) {
    int result = 0;
    for (Argument argument : arguments) {
      result = Math.max(result, argument.type().name().length());
    }
    return result;
  }

  private static int longestArgumentName(Collection<Argument> arguments) {
    int result = 0;
    for (Argument argument : arguments) {
      result = Math.max(result, argument.nameSanitized().length());
    }
    return result;
  }

  private static int longestArgumentNumber(Collection<Argument> arguments) {
    int maxNumber = 0;
    for (Argument argument : arguments) {
      maxNumber = Math.max(maxNumber, argument.number());
    }
    return Integer.toString(maxNumber).length();
  }
}
