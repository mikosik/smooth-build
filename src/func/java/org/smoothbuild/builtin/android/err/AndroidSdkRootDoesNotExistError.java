package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.builtin.android.EnvironmentVariable;
import org.smoothbuild.lang.message.Message;

public class AndroidSdkRootDoesNotExistError extends Message {
  public AndroidSdkRootDoesNotExistError(EnvironmentVariable androidSdkVar) {
    super(ERROR, createMessage(androidSdkVar));
  }

  private static String createMessage(EnvironmentVariable androidSdkVar) {
    StringBuilder builder = new StringBuilder();
    builder.append("Environment variable " + androidSdkVar.name()
        + "should contain absolute path to android SDK directory.\n");
    builder.append("It is set to '" + androidSdkVar.value() + "'\n");
    builder.append(" but such dir doesn't exist.\n");
    return builder.toString();
  }
}
