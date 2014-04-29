package org.smoothbuild.lang.function.def.args;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.util.LineBuilder;

public class ParamToArgMapToString {
  public static String toString(Map<Param, Arg> paramToArgMap) {
    int maxParamType = calculateLongestParamType(paramToArgMap.keySet());
    int maxParamName = calculateLongestParamName(paramToArgMap.keySet());
    int maxArgType = calculateLongestArgType(paramToArgMap.values());
    int maxArgName = calculateLongestArgName(paramToArgMap.values());
    int maxNumber = calculateLongestArgNumber(paramToArgMap.values());

    LineBuilder builder = new LineBuilder();
    for (Map.Entry<Param, Arg> entry : paramToArgMap.entrySet()) {
      String paramPart = entry.getKey().toPaddedString(maxParamType, maxParamName);
      Arg arg = entry.getValue();
      String argPart = arg.toPaddedString(maxArgType, maxArgName, maxNumber);
      builder.addLine("  " + paramPart + " <- " + argPart);
    }
    return builder.build();
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

  private static int calculateLongestArgType(Collection<Arg> args) {
    int result = 0;
    for (Arg arg : args) {
      result = Math.max(result, arg.type().name().length());
    }
    return result;
  }

  private static int calculateLongestArgName(Collection<Arg> args) {
    int result = 0;
    for (Arg arg : args) {
      result = Math.max(result, arg.nameSanitized().length());
    }
    return result;
  }

  private static int calculateLongestArgNumber(Collection<Arg> args) {
    int maxNumber = 0;
    for (Arg arg : args) {
      maxNumber = Math.max(maxNumber, arg.number());
    }
    return Integer.toString(maxNumber).length();
  }
}
