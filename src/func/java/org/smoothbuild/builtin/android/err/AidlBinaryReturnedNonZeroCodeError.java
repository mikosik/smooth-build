package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.builtin.android.AndroidSdk.AIDL_BINARY;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class AidlBinaryReturnedNonZeroCodeError extends Message {
  public AidlBinaryReturnedNonZeroCodeError(int exitValue) {
    super(ERROR, AIDL_BINARY + " binary returned non zero exit value = " + exitValue);
  }
}
