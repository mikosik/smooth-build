package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.db.Hash;

public class DecodeObjNodeExc extends DecodeObjExc {
  public DecodeObjNodeExc(Hash hash, CatB cat, String path, String message) {
    super(buildMessage(hash, cat, path, message));
  }

  public DecodeObjNodeExc(Hash hash, CatB cat, String path) {
    super(buildMessage(hash, cat, path, null));
  }

  public DecodeObjNodeExc(Hash hash, CatB cat, String path, Throwable e) {
    super(buildMessage(hash, cat, path, null), e);
  }

  private static String buildMessage(Hash hash, CatB cat, String path, String message) {
    return "Cannot decode " + cat.q() + " object at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
