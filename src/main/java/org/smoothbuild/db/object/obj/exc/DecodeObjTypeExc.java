package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjTypeExc extends DecodeObjExc {
  public DecodeObjTypeExc(Hash hash) {
    this(hash, null);
  }

  public DecodeObjTypeExc(Hash hash, Throwable e) {
    super("Cannot decode object at " + hash + ". Cannot decode its type.", e);
  }
}
