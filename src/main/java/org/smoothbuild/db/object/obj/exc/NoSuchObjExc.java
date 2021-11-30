package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;

public class NoSuchObjExc extends DecodeObjExc {
  public NoSuchObjExc(Hash hash) {
    this(hash, null);
  }

  public NoSuchObjExc(Hash hash, Throwable cause) {
    super("Cannot decode object at " + hash + ". Cannot find it in object db.", cause);
  }
}
