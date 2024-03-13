package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB;

public class DecodeCatNodeException extends DecodeCatException {
  public DecodeCatNodeException(Hash hash, CategoryKindB kind, String path, String message) {
    super(buildMessage(hash, kind, path, message));
  }

  public DecodeCatNodeException(Hash hash, CategoryKindB kind, String path) {
    super(buildMessage(hash, kind, path, null));
  }

  public DecodeCatNodeException(
      Hash hash, CategoryKindB kind, String path, int index, Throwable e) {
    this(hash, kind, path + "[" + index + "]", e);
  }

  public DecodeCatNodeException(Hash hash, CategoryKindB kind, String path, Throwable e) {
    super(buildMessage(hash, kind, path, null), e);
  }

  private static String buildMessage(Hash hash, CategoryKindB kind, String path, String message) {
    return "Cannot decode category at " + hash + " as " + kind.getClass().getSimpleName()
        + ". Cannot decode its node at `" + path + "` path in Merkle tree. "
        + (message != null ? message : "");
  }
}
