package org.smoothbuild.object.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

import com.google.common.hash.HashCode;

public class NoObjectWithGivenHashError extends Message {
  public NoObjectWithGivenHashError(HashCode hash) {
    super(ERROR, "Could not find object with hash = " + hash);
  }
}
