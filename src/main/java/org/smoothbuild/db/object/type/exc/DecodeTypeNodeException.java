package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeKindH;

public class DecodeTypeNodeException extends DecodeTypeException {
  public DecodeTypeNodeException(Hash hash, TypeKindH kind, String path, String message) {
    super(buildMessage(hash, kind, path, message));
  }

  public DecodeTypeNodeException(Hash hash, TypeKindH kind, String path) {
    super(buildMessage(hash, kind, path, null));
  }

  public DecodeTypeNodeException(Hash hash, TypeKindH kind, String path, int index, Throwable e) {
    this(hash, kind, path + "[" + index + "]", e);
  }

  public DecodeTypeNodeException(Hash hash, TypeKindH kind, String path, Throwable e) {
    super(buildMessage(hash, kind, path, null), e);
  }

  private static String buildMessage(Hash hash, TypeKindH kind, String path, String message) {
    return "Cannot decode " + kind.name() + " type at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
