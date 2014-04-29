package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.EnvironmentVariable;
import org.smoothbuild.util.LineBuilder;

@SuppressWarnings("serial")
public class AndroidSdkRootDoesNotExistError extends Message {
  public AndroidSdkRootDoesNotExistError(EnvironmentVariable androidSdkVar) {
    super(ERROR, createMessage(androidSdkVar));
  }

  private static String createMessage(EnvironmentVariable androidSdkVar) {
    LineBuilder b = new LineBuilder();

    b.addLine("Environment variable " + androidSdkVar.name()
        + "should contain absolute path to android SDK directory.");
    b.addLine("It is set to '" + androidSdkVar.value() + "'");
    b.addLine(" but such dir doesn't exist.");

    return b.build();
  }
}
