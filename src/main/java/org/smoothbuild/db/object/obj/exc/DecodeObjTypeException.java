package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjTypeException extends DecodeObjException {
  public DecodeObjTypeException(Hash hash) {
    this(hash, null);
  }

  public DecodeObjTypeException(Hash hash, Throwable e) {
    super("Cannot decode object at " + hash + ". Cannot decode its type.", e);
  }
}
