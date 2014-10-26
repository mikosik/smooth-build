package org.smoothbuild.lang.function.def.args.err;

import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Set;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.def.args.MapBuilder;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.util.LineBuilder;

@SuppressWarnings("serial")
public class MissingRequiredArgsError extends CodeMessage {
  public MissingRequiredArgsError(CodeLocation codeLocation, Function<?> function,
      MapBuilder mapBuilder, Set<Parameter> missingRequiredParameters) {
    super(ERROR, codeLocation,
        createMesssage(function, mapBuilder, missingRequiredParameters));
  }

  private static String createMesssage(Function<?> function,
      MapBuilder mapBuilder, Set<Parameter> missingRequiredParameters) {
    LineBuilder builder = new LineBuilder();

    builder.addLine("Not all parameters required by " + function.name()
        + " function has been specified.\n" + "Missing required parameters:");
    builder.add(parametersToString(missingRequiredParameters));
    builder.addLine("All correct 'parameters <- arguments' assignments:");
    builder.add(mapBuilder.toString());

    return builder.build();
  }
}
