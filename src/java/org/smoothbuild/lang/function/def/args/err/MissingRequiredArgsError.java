package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.lang.function.base.Param.paramsToString;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Set;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.args.ParamToArgMapBuilder;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class MissingRequiredArgsError extends CodeMessage {
  public MissingRequiredArgsError(CodeLocation codeLocation, Function function,
      ParamToArgMapBuilder paramToArgMapBuilder, Set<Param> missingRequiredParams) {
    super(ERROR, codeLocation, createMesssage(function, paramToArgMapBuilder, missingRequiredParams));
  }

  private static String createMesssage(Function function, ParamToArgMapBuilder paramToArgMapBuilder,
      Set<Param> missingRequiredParams) {
    StringBuilder builder = new StringBuilder();

    builder.append("Not all parameters required by " + function.name()
        + " function has been specified.\n" + "Missing required parameters:\n");
    builder.append(paramsToString(missingRequiredParams));
    builder.append("All correct 'parameters <- arguments' assignments:\n");
    builder.append(paramToArgMapBuilder.toString());

    return builder.toString();
  }
}
