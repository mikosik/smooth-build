package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.lang.function.base.Param.paramsToString;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Set;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.args.MapBuilder;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.util.LineBuilder;

@SuppressWarnings("serial")
public class MissingRequiredArgsError extends CodeMessage {
  public MissingRequiredArgsError(CodeLocation codeLocation, Function<?> function,
      MapBuilder mapBuilder, Set<Param> missingRequiredParams) {
    super(ERROR, codeLocation,
        createMesssage(function, mapBuilder, missingRequiredParams));
  }

  private static String createMesssage(Function<?> function,
      MapBuilder mapBuilder, Set<Param> missingRequiredParams) {
    LineBuilder builder = new LineBuilder();

    builder.addLine("Not all parameters required by " + function.name()
        + " function has been specified.\n" + "Missing required parameters:");
    builder.add(paramsToString(missingRequiredParams));
    builder.addLine("All correct 'parameters <- arguments' assignments:");
    builder.add(mapBuilder.toString());

    return builder.build();
  }
}
