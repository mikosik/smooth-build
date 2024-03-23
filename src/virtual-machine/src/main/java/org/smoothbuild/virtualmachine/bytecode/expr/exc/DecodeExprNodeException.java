package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;

public class DecodeExprNodeException extends DecodeExprException {
  public DecodeExprNodeException(Hash hash, BCategory cat, String path, String message) {
    super(buildMessage(hash, cat, path, message));
  }

  public DecodeExprNodeException(Hash hash, BCategory cat, String path) {
    super(buildMessage(hash, cat, path, null));
  }

  public DecodeExprNodeException(Hash hash, BCategory cat, String path, Throwable e) {
    super(buildMessage(hash, cat, path, null), e);
  }

  private static String buildMessage(Hash hash, BCategory cat, String path, String message) {
    return "Cannot decode " + cat.q() + " object at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
