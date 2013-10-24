package org.smoothbuild.object.err;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;

import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;

public class ReadingHashedObjectFailedError extends Message {
  public ReadingHashedObjectFailedError(HashCode hash, Exception e) {
    super(MessageType.ERROR, "IO error occurred while reading object with hash = " + hash + "\n"
        + Throwables.getStackTraceAsString(e));
  }
}
