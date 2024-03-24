package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;

public class DecodeExprNodeException extends DecodeExprException {
  public DecodeExprNodeException(Hash hash, BKind kind, String path, String message) {
    super(buildMessage(hash, kind, path, message));
  }

  public DecodeExprNodeException(Hash hash, BKind kind, String path) {
    super(buildMessage(hash, kind, path, null));
  }

  public DecodeExprNodeException(Hash hash, BKind kind, String path, Throwable e) {
    super(buildMessage(hash, kind, path, null), e);
  }

  private static String buildMessage(Hash hash, BKind kind, String path, String message) {
    return "Cannot decode " + kind.q() + " object at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
