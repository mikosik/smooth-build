package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.def.args.Argument;
import org.smoothbuild.lang.function.def.args.MapToString;
import org.smoothbuild.lang.function.def.args.TypedParametersPool;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.util.LineBuilder;

@SuppressWarnings("serial")
public class AmbiguousNamelessArgsError extends CodeMessage {

  public AmbiguousNamelessArgsError(Name functionName, Map<Parameter, Argument> paramToArgMap,
      Collection<Argument> availableArguments, TypedParametersPool availableTypedParams) {
    this(functionName, paramToArgMap, Argument.NUMBER_ORDERING.sortedCopy(availableArguments),
        availableTypedParams);
  }

  public AmbiguousNamelessArgsError(Name functionName, Map<Parameter, Argument> paramToArgMap,
      List<Argument> availableArguments, TypedParametersPool availableTypedParams) {
    super(ERROR, availableArguments.iterator().next().codeLocation(), message(functionName,
        paramToArgMap, availableArguments, availableTypedParams));
  }

  private static String message(Name functionName, Map<Parameter, Argument> paramToArgMap,
      List<Argument> availableArguments, TypedParametersPool availableTypedParams) {
    String assignmentList = MapToString.toString(paramToArgMap);
    if (availableTypedParams.isEmpty()) {
      return "Can't find parameter(s) of proper type in " + functionName
          + " function for some nameless argument(s):\n"
          + "List of assignments that were successfully detected so far is following:\n"
          + assignmentList
          + "List of arguments for which no parameter could be found is following:\n"
          + argsToList(availableArguments);
    } else {
      return "Can't decide unambiguously to which parameters in " + functionName
          + " function some nameless arguments should be assigned:\n"
          + "List of assignments that were successfully detected is following:\n" + assignmentList
          + "List of nameless arguments that caused problems:\n" + argsToList(availableArguments)
          + "List of unassigned parameters of desired type is following:\n"
          + availableTypedParams.toFormattedString();
    }
  }

  private static String argsToList(List<Argument> arguments) {
    int typeLength = longestArgType(arguments);
    int nameLength = longestArgName(arguments);
    int numberLength = longestArgNumber(arguments);

    LineBuilder builder = new LineBuilder();
    for (Argument argument : arguments) {
      builder.addLine("  " + argument.toPaddedString(typeLength, nameLength, numberLength));
    }
    return builder.build();
  }

  private static int longestArgType(List<Argument> arguments) {
    int result = 0;
    for (Argument argument : arguments) {
      result = Math.max(result, argument.type().name().length());
    }
    return result;
  }

  private static int longestArgName(List<Argument> arguments) {
    int result = 0;
    for (Argument argument : arguments) {
      result = Math.max(result, argument.nameSanitized().length());
    }
    return result;
  }

  private static int longestArgNumber(List<Argument> arguments) {
    int maxNumber = 0;
    for (Argument argument : arguments) {
      maxNumber = Math.max(maxNumber, argument.number());
    }
    return Integer.toString(maxNumber).length();
  }
}
