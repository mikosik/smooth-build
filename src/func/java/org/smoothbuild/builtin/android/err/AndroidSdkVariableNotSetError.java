package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.builtin.android.EnvironmentVariable;
import org.smoothbuild.message.base.Message;

public class AndroidSdkVariableNotSetError extends Message {
  public AndroidSdkVariableNotSetError(EnvironmentVariable androidSdkVar) {
    super(ERROR, "Environment variable " + androidSdkVar.name() + " is not set.\n"
        + "It should contain absolute path to android SDK.");
  }
}
