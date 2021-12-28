package org.smoothbuild.db.bytecode.obj.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeObjCatExc extends DecodeObjExc {
  public DecodeObjCatExc(Hash hash) {
    this(hash, null);
  }

  public DecodeObjCatExc(Hash hash, Throwable e) {
    super("Cannot decode object at " + hash + ". Cannot decode its category.", e);
  }
}
