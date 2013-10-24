package org.smoothbuild.object.err;

import java.io.IOException;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;

import com.google.common.base.Throwables;
import com.google.common.hash.HashCode;

public class WritingHashedObjectFailedError extends Message {
  public WritingHashedObjectFailedError(HashCode hash, IOException e) {
    super(MessageType.ERROR, "IO error occurred while writing object with hash = " + hash + "\n"
        + Throwables.getStackTraceAsString(e));
  }
}
