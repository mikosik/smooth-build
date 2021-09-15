package org.smoothbuild.db.hashed.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeByteException extends HashedDbException {
  public DecodeByteException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as byte.");
  }
}

