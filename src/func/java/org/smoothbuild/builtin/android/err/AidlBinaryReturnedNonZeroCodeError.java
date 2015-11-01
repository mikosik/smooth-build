package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.builtin.android.AndroidSdk.AIDL_BINARY;
import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.lang.message.Message;

public class AidlBinaryReturnedNonZeroCodeError extends Message {
  public AidlBinaryReturnedNonZeroCodeError(int exitValue) {
    super(ERROR, AIDL_BINARY + " binary returned non zero exit value = " + exitValue);
  }
}
