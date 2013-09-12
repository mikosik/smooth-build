package org.smoothbuild.parse.def.err;

import java.util.List;
import java.util.Set;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.parse.def.Argument;
import org.smoothbuild.parse.def.AssignmentList;
import org.smoothbuild.problem.CodeError;

public class AmbiguousNamelessArgsError extends CodeError {

  public AmbiguousNamelessArgsError(AssignmentList assignmentList, Set<Argument> availableArgs,
      Set<Param> availableParams) {
    this(assignmentList, Argument.NUMBER_ORDERING.sortedCopy(availableArgs), availableParams);
  }

  public AmbiguousNamelessArgsError(AssignmentList assignmentList, List<Argument> availableArgs,
      Set<Param> availableParams) {
    super(availableArgs.iterator().next().codeLocation(), message(assignmentList, availableArgs,
        availableParams));
  }

  private static String message(AssignmentList assignmentList, List<Argument> availableArgs,
      Set<Param> availableParams) {
    if (availableParams.size() == 0) {
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
          + paramsToList(availableParams);
    }
  }

  private static String paramsToList(Set<Param> params) {
    int typeLength = longestParamType(params);
    int nameLength = longestParamName(params);

    StringBuilder builder = new StringBuilder();
    for (Param param : params) {
      builder.append("  " + param.toPaddedString(typeLength, nameLength) + "\n");
    }
    return builder.toString();
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

  private static int longestParamType(Set<Param> params) {
    int result = 0;
    for (Param param : params) {
      result = Math.max(result, param.type().name().length());
    }
    return result;
  }

  private static int longestParamName(Set<Param> params) {
    int result = 0;
    for (Param param : params) {
      result = Math.max(result, param.name().length());
    }
    return result;
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
