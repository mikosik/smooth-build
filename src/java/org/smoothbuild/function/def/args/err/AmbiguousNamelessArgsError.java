package org.smoothbuild.function.def.args.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import java.util.List;
import java.util.Set;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.function.def.args.AssignmentList;
import org.smoothbuild.function.def.args.TypedParamsPool;
import org.smoothbuild.message.message.CodeMessage;

public class AmbiguousNamelessArgsError extends CodeMessage {

  public AmbiguousNamelessArgsError(Name functionName, AssignmentList assignmentList,
      Set<Argument> availableArgs, TypedParamsPool availableTypedParams) {
    this(functionName, assignmentList, Argument.NUMBER_ORDERING.sortedCopy(availableArgs),
        availableTypedParams);
  }

  public AmbiguousNamelessArgsError(Name functionName, AssignmentList assignmentList,
      List<Argument> availableArgs, TypedParamsPool availableTypedParams) {
    super(ERROR, availableArgs.iterator().next().codeLocation(), message(functionName,
        assignmentList, availableArgs, availableTypedParams));
  }

  private static String message(Name functionName, AssignmentList assignmentList,
      List<Argument> availableArgs, TypedParamsPool availableTypedParams) {
    if (availableTypedParams.size() == 0) {
      return "Can't find parameter(s) of proper type in '" + functionName.simple()
          + "' function for some nameless argument(s):\n"
          + "List of assignments that were successfully detected so far is following:\n"
          + assignmentList.toString()
          + "List of arguments for which no parameter could be found is following:\n"
          + argsToList(availableArgs);
    } else {
      return "Can't decide unambiguously to which parameters in '" + functionName.simple()
          + "' function some nameless arguments should be assigned:\n"
          + "List of assignments that were successfully detected is following:\n"
          + assignmentList.toString() + "List of nameless arguments that caused problems:\n"
          + argsToList(availableArgs)
          + "List of unassigned parameters of desired type is following:\n"
          + availableTypedParams.toFormattedString();
    }
  }

  private static String argsToList(List<Argument> args) {
    int typeLength = longestArgType(args);
    int nameLength = longestArgName(args);
    int numberLength = longestArgNumber(args);

    StringBuilder builder = new StringBuilder();
    for (Argument arg : args) {
      builder.append("  " + arg.toPaddedString(typeLength, nameLength, numberLength) + "\n");
    }
    return builder.toString();
  }

  private static int longestArgType(List<Argument> args) {
    int result = 0;
    for (Argument arg : args) {
      result = Math.max(result, arg.type().name().length());
    }
    return result;
  }

  private static int longestArgName(List<Argument> args) {
    int result = 0;
    for (Argument arg : args) {
      result = Math.max(result, arg.nameSanitized().length());
    }
    return result;
  }

  private static int longestArgNumber(List<Argument> args) {
    int maxNumber = 0;
    for (Argument arg : args) {
      maxNumber = Math.max(maxNumber, arg.number());
    }
    return Integer.toString(maxNumber).length();
  }
}
