package org.smoothbuild.task;

import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.Info;

@SuppressWarnings("serial")
public class PluginTaskCompletedMessage extends Info {
  public PluginTaskCompletedMessage(Signature signature) {
    super(createMessage(signature));
  }

  private static String createMessage(Signature signature) {
    return padEnd("[" + signature.name().simple() + "]", 10, ' ') + " DONE";
  }
}
