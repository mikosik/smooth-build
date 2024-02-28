package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.common.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;

public class DecodeExprNodeException extends DecodeExprException {
  public DecodeExprNodeException(Hash hash, CategoryB cat, String path, String message) {
    super(buildMessage(hash, cat, path, message));
  }

  public DecodeExprNodeException(Hash hash, CategoryB cat, String path) {
    super(buildMessage(hash, cat, path, null));
  }

  public DecodeExprNodeException(Hash hash, CategoryB cat, String path, Throwable e) {
    super(buildMessage(hash, cat, path, null), e);
  }

  private static String buildMessage(Hash hash, CategoryB cat, String path, String message) {
    return "Cannot decode " + cat.q() + " object at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
