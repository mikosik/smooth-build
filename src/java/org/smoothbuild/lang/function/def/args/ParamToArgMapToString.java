package org.smoothbuild.lang.function.def.args;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Param;

public class ParamToArgMapToString {
  public static String toString(Map<Param, Argument> paramToArgMap) {
    int maxParamType = calculateLongestParamType(paramToArgMap.keySet());
    int maxParamName = calculateLongestParamName(paramToArgMap.keySet());
    int maxArgType = calculateLongestArgType(paramToArgMap.values());
    int maxArgName = calculateLongestArgName(paramToArgMap.values());
    int maxNumber = calculateLongestArgNumber(paramToArgMap.values());
    StringBuilder builder = new StringBuilder();

    for (Map.Entry<Param, Argument> entry : paramToArgMap.entrySet()) {
      String paramPart = entry.getKey().toPaddedString(maxParamType, maxParamName);
      Argument argument = entry.getValue();
      String argPart = argument.toPaddedString(maxArgType, maxArgName, maxNumber);
      builder.append("  " + paramPart + " <- " + argPart + "\n");
    }
    return builder.toString();
  }

  private static int calculateLongestParamType(Set<Param> params) {
    int result = 0;
    for (Param param : params) {
      result = Math.max(result, param.type().name().length());
    }
    return result;
  }

  private static int calculateLongestParamName(Set<Param> params) {
    int result = 0;
    for (Param param : params) {
      result = Math.max(result, param.name().length());
    }
    return result;
  }

  private static int calculateLongestArgType(Collection<Argument> args) {
    int result = 0;
    for (Argument arg : args) {
      result = Math.max(result, arg.type().name().length());
    }
    return result;
  }

  private static int calculateLongestArgName(Collection<Argument> args) {
    int result = 0;
    for (Argument arg : args) {
      result = Math.max(result, arg.nameSanitized().length());
    }
    return result;
  }

  private static int calculateLongestArgNumber(Collection<Argument> args) {
    int maxNumber = 0;
    for (Argument arg : args) {
      maxNumber = Math.max(maxNumber, arg.number());
    }
    return Integer.toString(maxNumber).length();
  }
}
