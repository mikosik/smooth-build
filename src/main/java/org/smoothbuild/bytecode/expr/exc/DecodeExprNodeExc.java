package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CatB;

public class DecodeExprNodeExc extends DecodeExprExc {
  public DecodeExprNodeExc(Hash hash, CatB cat, String path, String message) {
    super(buildMessage(hash, cat, path, message));
  }

  public DecodeExprNodeExc(Hash hash, CatB cat, String path) {
    super(buildMessage(hash, cat, path, null));
  }

  public DecodeExprNodeExc(Hash hash, CatB cat, String path, Throwable e) {
    super(buildMessage(hash, cat, path, null), e);
  }

  private static String buildMessage(Hash hash, CatB cat, String path, String message) {
    return "Cannot decode " + cat.q() + " object at " + hash + ". Cannot decode its node at `"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
