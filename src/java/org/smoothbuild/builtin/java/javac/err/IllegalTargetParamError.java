package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import java.util.Set;

import org.smoothbuild.message.message.Message;

public class IllegalTargetParamError extends Message {
  public IllegalTargetParamError(String value, Set<String> allowed) {
    super(ERROR, createMessage(value, allowed));
  }

  private static String createMessage(String value, Set<String> allowed) {
    StringBuilder builder = new StringBuilder();

    builder.append("Parameter target has illegal value = '" + value + "'.\n");
    builder.append("Only following values are allowed " + allowed);

    return builder.toString();
  }
}
