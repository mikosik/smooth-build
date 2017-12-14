package org.smoothbuild.parse.arg;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.smoothbuild.lang.function.base.ParameterInfo;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.CallNode;

public class ArgsStringHelper {
  public static String assignedArgsToString(CallNode call) {
    Map<ParameterInfo, ArgNode> paramToArgMap = createMap(call);
    int maxParamType = ParameterInfo.longestType(paramToArgMap.keySet());
    int maxParamName = ParameterInfo.longestName(paramToArgMap.keySet());
    int maxArgType = longestArgType(paramToArgMap.values());
    int maxArgName = longestArgName(paramToArgMap.values());
    int maxPosition = longestArgPosition(paramToArgMap.values());

    return paramToArgMap.entrySet()
        .stream()
        .map(e -> {
          String paramPart = e.getKey().toPaddedString(maxParamType, maxParamName);
          String argPart = e.getValue().toPaddedString(maxArgType, maxArgName, maxPosition);
          return "  " + paramPart + " <- " + argPart + "\n";
        })
        .sorted()
        .collect(joining());
  }

  private static Map<ParameterInfo, ArgNode> createMap(CallNode call) {
    return call
        .args()
        .stream()
        .filter(a -> a.has(ParameterInfo.class))
        .collect(toMap(a -> a.get(ParameterInfo.class), Function.identity()));
  }

  public static String argsToString(Collection<ArgNode> availableArgs) {
    List<ArgNode> args = ArgNode.POSITION_ORDERING.sortedCopy(availableArgs);
    int typeLength = longestArgType(args);
    int nameLength = longestArgName(args);
    int positionLength = longestArgPosition(args);

    return args
        .stream()
        .map(a -> "  " + a.toPaddedString(typeLength, nameLength, positionLength) + "\n")
        .sorted()
        .collect(joining());
  }

  private static int longestArgType(Collection<ArgNode> args) {
    int result = 0;
    for (ArgNode arg : args) {
      result = Math.max(result, arg.get(Type.class).name().length());
    }
    return result;
  }

  private static int longestArgName(Collection<ArgNode> args) {
    int result = 0;
    for (ArgNode arg : args) {
      result = Math.max(result, arg.nameSanitized().length());
    }
    return result;
  }

  private static int longestArgPosition(Collection<ArgNode> args) {
    int maxPosition = 0;
    for (ArgNode arg : args) {
      maxPosition = Math.max(maxPosition, arg.position());
    }
    return Integer.toString(maxPosition).length();
  }
}
