package org.smoothbuild.db.hash.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class HashedDbError extends Message {
  public HashedDbError(String message) {
    super(ERROR, "Internal error in smooth hashed DB:\n" + message);
  }
}
