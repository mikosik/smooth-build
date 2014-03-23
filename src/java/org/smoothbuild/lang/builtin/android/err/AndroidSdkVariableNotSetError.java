package org.smoothbuild.lang.builtin.android.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.EnvironmentVariable;

@SuppressWarnings("serial")
public class AndroidSdkVariableNotSetError extends Message {
  public AndroidSdkVariableNotSetError(EnvironmentVariable androidSdkVar) {
    super(ERROR, "Environment variable " + androidSdkVar.name() + " is not set.\n"
        + "It should contain absolute path to android SDK.");
  }
}
