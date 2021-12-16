package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjNoSuchObjExc extends DecodeObjExc {
  public DecodeObjNoSuchObjExc(Hash hash) {
    this(hash, null);
  }

  public DecodeObjNoSuchObjExc(Hash hash, Throwable cause) {
    super("Cannot decode object at " + hash + ". Cannot find it in object db.", cause);
  }
}
