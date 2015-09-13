package org.smoothbuild.lang.function.def.err;

import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.def.Argument;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;

public class MissingRequiredArgsError extends CodeMessage {
  public MissingRequiredArgsError(CodeLocation codeLocation, Function function,
      Map<Parameter, Argument> mapBuilder, Set<Parameter> missingRequiredParameters) {
    super(ERROR, codeLocation, createMesssage(function, mapBuilder, missingRequiredParameters));
  }

  private static String createMesssage(Function function, Map<Parameter, Argument> mapBuilder,
      Set<Parameter> missingRequiredParameters) {
    StringBuilder builder = new StringBuilder();
    builder.append("Not all parameters required by " + function.name()
        + " function has been specified.\n" + "Missing required parameters:\n");
    builder.append(parametersToString(missingRequiredParameters));
    builder.append("All correct 'parameters <- arguments' assignments:\n");
    builder.append(mapBuilder.toString());
    return builder.toString();
  }
}
