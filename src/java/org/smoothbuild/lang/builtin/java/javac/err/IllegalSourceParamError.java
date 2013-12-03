package org.smoothbuild.lang.builtin.java.javac.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Set;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.LineBuilder;

public class IllegalSourceParamError extends Message {
  public IllegalSourceParamError(String value, Set<String> allowed) {
    super(ERROR, createMessage(value, allowed));
  }

  private static String createMessage(String value, Set<String> allowed) {
    LineBuilder builder = new LineBuilder();

    builder.addLine("Parameter source has illegal value = '" + value + "'.");
    builder.add("Only following values are allowed " + allowed);

    return builder.build();
  }
}
