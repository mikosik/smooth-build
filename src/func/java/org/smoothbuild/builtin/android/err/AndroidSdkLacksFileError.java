package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.nio.file.Path;

import org.smoothbuild.builtin.android.EnvironmentVariable;
import org.smoothbuild.message.base.Message;

public class AndroidSdkLacksFileError extends Message {
  public AndroidSdkLacksFileError(EnvironmentVariable androidSdkVar, Path requiredSdkFile) {
    super(ERROR, createMessage(androidSdkVar, requiredSdkFile));
  }

  private static String createMessage(EnvironmentVariable androidSdkVar, Path requiredSdkFile) {
    StringBuilder builder = new StringBuilder();
    builder.append("Can't find " + requiredSdkFile + " file in android sdk.\n");
    builder.append("Path to Android SDK was set by the following environment variable:\n");
    builder.append(androidSdkVar.toString() + "\n");
    return builder.toString();
  }
}
