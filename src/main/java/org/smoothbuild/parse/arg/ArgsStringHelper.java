package org.smoothbuild.parse.arg;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.smoothbuild.lang.function.base.TypedName;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.CallNode;

public class ArgsStringHelper {
  public static String assignedArgsToString(CallNode call) {
    Map<TypedName, ArgNode> paramToArgMap = createMap(call);
    int maxParamType = TypedName.longestType(paramToArgMap.keySet());
    int maxParamName = TypedName.longestName(paramToArgMap.keySet());
    int maxArgType = longestArgType(paramToArgMap.values());
    int maxArgName = longestArgName(paramToArgMap.values());
    int maxPosition = longestArgPosition(paramToArgMap.values());

    StringBuilder builder = new StringBuilder();
    for (Map.Entry<TypedName, ArgNode> entry : paramToArgMap.entrySet()) {
      String paramPart = entry.getKey().toPaddedString(maxParamType, maxParamName);
      ArgNode arg = entry.getValue();
      String argPart = arg.toPaddedString(maxArgType, maxArgName, maxPosition);
      builder.append("  " + paramPart + " <- " + argPart + "\n");
    }
    return builder.toString();
  }

  private static Map<TypedName, ArgNode> createMap(CallNode call) {
    return call
        .args()
        .stream()
        .filter(a -> a.has(TypedName.class))
        .collect(toMap(a -> a.get(TypedName.class), Function.identity()));
  }

  public static String argsToString(Collection<ArgNode> availableArgs) {
    List<ArgNode> args = ArgNode.POSITION_ORDERING.sortedCopy(availableArgs);
    int typeLength = longestArgType(args);
    int nameLength = longestArgName(args);
    int positionLength = longestArgPosition(args);

    StringBuilder builder = new StringBuilder();
    for (ArgNode arg : args) {
      builder.append("  " + arg.toPaddedString(
          typeLength, nameLength, positionLength) + "\n");
    }
    return builder.toString();
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
