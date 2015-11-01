package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.builtin.android.EnvironmentVariable;
import org.smoothbuild.lang.message.Message;

public class AndroidSdkVariableNotSetError extends Message {
  public AndroidSdkVariableNotSetError(EnvironmentVariable androidSdkVar) {
    super(ERROR, "Environment variable " + androidSdkVar.name() + " is not set.\n"
        + "It should contain absolute path to android SDK.");
  }
}
