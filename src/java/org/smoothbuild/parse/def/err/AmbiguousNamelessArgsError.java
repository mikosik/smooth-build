package org.smoothbuild.parse.def.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import java.util.List;
import java.util.Set;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.parse.def.Argument;
import org.smoothbuild.parse.def.AssignmentList;
import org.smoothbuild.parse.def.TypedParamsPool;

public class AmbiguousNamelessArgsError extends CodeMessage {

  public AmbiguousNamelessArgsError(AssignmentList assignmentList, Set<Argument> availableArgs,
      TypedParamsPool availableTypedParams) {
    this(assignmentList, Argument.NUMBER_ORDERING.sortedCopy(availableArgs), availableTypedParams);
  }

  public AmbiguousNamelessArgsError(AssignmentList assignmentList, List<Argument> availableArgs,
      TypedParamsPool availableTypedParams) {
    super(ERROR, availableArgs.iterator().next().codeLocation(), message(assignmentList,
        availableArgs, availableTypedParams));
  }

  private static String message(AssignmentList assignmentList, List<Argument> availableArgs,
      TypedParamsPool availableTypedParams) {
    if (availableTypedParams.size() == 0) {
      return "Couldn't find parameter(s) of proper type for some nameless argument(s):\n"
          + "List of assignments that were successfully detected so far is following:\n"
          + assignmentList.toString()
          + "List of arguments for which no parameter could be found is following:\n"
          + argsToList(availableArgs);
    } else {
      return "Couldn't decide unambiguously to which parameters some nameless arguments should be assigned:\n"
          + "List of assignments that were successfully detected is following:\n"
          + assignmentList.toString()
          + "List of nameless arguments that caused problems:\n"
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
