package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.SpecH;

public class DecodeObjNodeExc extends DecodeObjExc {
  public DecodeObjNodeExc(Hash hash, SpecH type, String path, String message) {
    super(buildMessage(hash, type, path, message));
  }

  public DecodeObjNodeExc(Hash hash, SpecH type, String path) {
    super(buildMessage(hash, type, path, null));
  }

  public DecodeObjNodeExc(Hash hash, SpecH type, String path, Throwable e) {
    super(buildMessage(hash, type, path, null), e);
  }

  private static String buildMessage(Hash hash, SpecH type, String path, String message) {
    return "Cannot decode " + type.q() + " object at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
