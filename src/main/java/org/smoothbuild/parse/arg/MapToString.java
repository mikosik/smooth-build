package org.smoothbuild.parse.arg;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Parameter;

public class MapToString {
  public static String toString(Map<Parameter, Argument> paramToArgMap) {
    int maxParamType = longestParameterType(paramToArgMap.keySet());
    int maxParamName = longestParameterName(paramToArgMap.keySet());
    int maxArgType = longestArgumentType(paramToArgMap.values());
    int maxArgName = longestArgumentName(paramToArgMap.values());
    int maxPosition = longestArgumentPosition(paramToArgMap.values());

    StringBuilder builder = new StringBuilder();
    for (Map.Entry<Parameter, Argument> entry : paramToArgMap.entrySet()) {
      String paramPart = entry.getKey().toPaddedString(maxParamType, maxParamName);
      Argument argument = entry.getValue();
      String argPart = argument.toPaddedString(maxArgType, maxArgName, maxPosition);
      builder.append("  " + paramPart + " <- " + argPart + "\n");
    }
    return builder.toString();
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

  private static int longestArgumentPosition(Collection<Argument> arguments) {
    int maxPosition = 0;
    for (Argument argument : arguments) {
      maxPosition = Math.max(maxPosition, argument.position());
    }
    return Integer.toString(maxPosition).length();
  }
}
