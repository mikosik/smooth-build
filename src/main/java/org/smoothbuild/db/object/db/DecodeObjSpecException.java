package org.smoothbuild.db.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjSpecException extends DecodeObjException {
  public DecodeObjSpecException(Hash hash) {
    this(hash, null);
  }

  public DecodeObjSpecException(Hash hash, Throwable e) {
    super("Cannot decode object at " + hash + ". Cannot decode its spec.", e);
  }
}
