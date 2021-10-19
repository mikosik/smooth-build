package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;

public class DecodeObjNodeException extends DecodeObjException {
  public DecodeObjNodeException(Hash hash, Spec spec, String path, String message) {
    super(buildMessage(hash, spec, path, message));
  }

  public DecodeObjNodeException(Hash hash, Spec spec, String path) {
    super(buildMessage(hash, spec, path, null));
  }

  public DecodeObjNodeException(Hash hash, Spec spec, String path, Throwable e) {
    super(buildMessage(hash, spec, path, null), e);
  }

  private static String buildMessage(Hash hash, Spec spec, String path, String message) {
    return "Cannot decode " + spec.q() + " object at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
