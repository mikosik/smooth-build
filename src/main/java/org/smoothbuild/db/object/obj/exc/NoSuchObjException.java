package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;

public class NoSuchObjException extends DecodeObjException {
  public NoSuchObjException(Hash hash) {
    this(hash, null);
  }

  public NoSuchObjException(Hash hash, Throwable cause) {
    super("Cannot decode object at " + hash + ". Cannot find it in object db.", cause);
  }
}
