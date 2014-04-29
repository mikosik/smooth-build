package org.smoothbuild.lang.builtin.android.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.nio.file.Path;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.EnvironmentVariable;
import org.smoothbuild.util.LineBuilder;

@SuppressWarnings("serial")
public class AndroidSdkLacksFileError extends Message {
  public AndroidSdkLacksFileError(EnvironmentVariable androidSdkVar, Path requiredSdkFile) {
    super(ERROR, createMessage(androidSdkVar, requiredSdkFile));
  }

  private static String createMessage(EnvironmentVariable androidSdkVar, Path requiredSdkFile) {
    LineBuilder b = new LineBuilder();

    b.addLine("Can't find " + requiredSdkFile + " file in android sdk.");
    b.addLine("Path to Android SDK was set by the following environment variable:");
    b.addLine(androidSdkVar.toString());

    return b.build();
  }
}
