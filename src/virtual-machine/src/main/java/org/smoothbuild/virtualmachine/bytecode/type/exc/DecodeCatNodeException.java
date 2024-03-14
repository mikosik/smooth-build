package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryId;

public class DecodeCatNodeException extends DecodeCatException {
  public DecodeCatNodeException(Hash hash, CategoryId categoryId, String path) {
    this(hash, categoryId, path, (String) null);
  }

  public DecodeCatNodeException(Hash hash, CategoryId categoryId, String path, String message) {
    super(buildMessage(hash, categoryId, path, message));
  }

  public DecodeCatNodeException(
      Hash hash, CategoryId categoryId, String path, int index, Throwable e) {
    this(hash, categoryId, path + "[" + index + "]", e);
  }

  public DecodeCatNodeException(Hash hash, CategoryId categoryId, String path, Throwable e) {
    super(buildMessage(hash, categoryId, path, null), e);
  }

  private static String buildMessage(Hash hash, CategoryId id, String path, String message) {
    return "Cannot decode category " + id + " at " + hash + ". Cannot decode its node at " + "`"
        + path + "` path in Merkle tree. " + (message != null ? message : "");
  }
}
