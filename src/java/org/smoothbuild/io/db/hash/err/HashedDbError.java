package org.smoothbuild.io.db.hash.err;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.message.base.Message;

public class HashedDbError extends Message {
  public HashedDbError(String message) {
    super(FATAL, "Internal error in smooth hashed DB:\n" + message);
  }
}
