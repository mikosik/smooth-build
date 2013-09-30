package org.smoothbuild.parse.def.err;

import static org.smoothbuild.function.base.Param.paramsToString;
import static org.smoothbuild.message.message.MessageType.ERROR;

import java.util.Set;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.parse.def.AssignmentList;

public class MissingRequiredArgsError extends CodeMessage {
  public MissingRequiredArgsError(CodeLocation codeLocation, Function function,
      AssignmentList assignmentList, Set<Param> missingRequiredParams) {
    super(ERROR, codeLocation, createMesssage(function, assignmentList, missingRequiredParams));
  }

  private static String createMesssage(Function function, AssignmentList assignmentList,
      Set<Param> missingRequiredParams) {
    StringBuilder builder = new StringBuilder();

    builder.append("Not all parameters required by " + function.name()
        + " function has been specified.\n" + "Missing required parameters:\n");
    builder.append(paramsToString(missingRequiredParams));
    builder.append("All correct 'parameters <- arguments' assignments:\n");
    builder.append(assignmentList.toString());

    return builder.toString();
  }
}
