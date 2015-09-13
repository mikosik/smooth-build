package org.smoothbuild.builtin.java.javac.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Set;

import org.smoothbuild.message.base.Message;

public class IllegalSourceParamError extends Message {
  public IllegalSourceParamError(String value, Set<String> allowed) {
    super(ERROR, createMessage(value, allowed));
  }

  private static String createMessage(String value, Set<String> allowed) {
    StringBuilder builder = new StringBuilder();
    builder.append("Parameter source has illegal value = '" + value + "'.\n");
    builder.append("Only following values are allowed " + allowed + "\n");
    return builder.toString();
  }
}
