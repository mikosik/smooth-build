package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;

public class DecodeObjNodeException extends DecodeObjException {
  public DecodeObjNodeException(Hash hash, TypeH type, String path, String message) {
    super(buildMessage(hash, type, path, message));
  }

  public DecodeObjNodeException(Hash hash, TypeH type, String path) {
    super(buildMessage(hash, type, path, null));
  }

  public DecodeObjNodeException(Hash hash, TypeH type, String path, Throwable e) {
    super(buildMessage(hash, type, path, null), e);
  }

  private static String buildMessage(Hash hash, TypeH type, String path, String message) {
    return "Cannot decode " + type.q() + " object at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
