package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.lang.function.def.args.ParamToArgMapToString;
import org.smoothbuild.lang.function.def.args.TypedParamsPool;
import org.smoothbuild.message.base.CodeMessage;

public class AmbiguousNamelessArgsError extends CodeMessage {

  public AmbiguousNamelessArgsError(Name functionName, Map<Param, Arg> paramToArgMap,
      Set<Arg> availableArgs, TypedParamsPool availableTypedParams) {
    this(functionName, paramToArgMap, Arg.NUMBER_ORDERING.sortedCopy(availableArgs),
        availableTypedParams);
  }

  public AmbiguousNamelessArgsError(Name functionName, Map<Param, Arg> paramToArgMap,
      List<Arg> availableArgs, TypedParamsPool availableTypedParams) {
    super(ERROR, availableArgs.iterator().next().codeLocation(), message(functionName,
        paramToArgMap, availableArgs, availableTypedParams));
  }

  private static String message(Name functionName, Map<Param, Arg> paramToArgMap,
      List<Arg> availableArgs, TypedParamsPool availableTypedParams) {
    String assignmentList = ParamToArgMapToString.toString(paramToArgMap);
    if (availableTypedParams.size() == 0) {
      return "Can't find parameter(s) of proper type in " + functionName
          + " function for some nameless argument(s):\n"
          + "List of assignments that were successfully detected so far is following:\n"
          + assignmentList
          + "List of arguments for which no parameter could be found is following:\n"
          + argsToList(availableArgs);
    } else {
      return "Can't decide unambiguously to which parameters in " + functionName
          + " function some nameless arguments should be assigned:\n"
          + "List of assignments that were successfully detected is following:\n" + assignmentList
          + "List of nameless arguments that caused problems:\n" + argsToList(availableArgs)
          + "List of unassigned parameters of desired type is following:\n"
          + availableTypedParams.toFormattedString();
    }
  }

  private static String argsToList(List<Arg> args) {
    int typeLength = longestArgType(args);
    int nameLength = longestArgName(args);
    int numberLength = longestArgNumber(args);

    StringBuilder builder = new StringBuilder();
    for (Arg arg : args) {
      builder.append("  " + arg.toPaddedString(typeLength, nameLength, numberLength) + "\n");
    }
    return builder.toString();
  }

  private static int longestArgType(List<Arg> args) {
    int result = 0;
    for (Arg arg : args) {
      result = Math.max(result, arg.type().name().length());
    }
    return result;
  }

  private static int longestArgName(List<Arg> args) {
    int result = 0;
    for (Arg arg : args) {
      result = Math.max(result, arg.nameSanitized().length());
    }
    return result;
  }

  private static int longestArgNumber(List<Arg> args) {
    int maxNumber = 0;
    for (Arg arg : args) {
      maxNumber = Math.max(maxNumber, arg.number());
    }
    return Integer.toString(maxNumber).length();
  }
}
