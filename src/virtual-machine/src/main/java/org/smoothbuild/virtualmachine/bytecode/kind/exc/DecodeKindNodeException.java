package org.smoothbuild.virtualmachine.bytecode.kind.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.KindId;

public class DecodeKindNodeException extends DecodeKindException {
  public DecodeKindNodeException(Hash hash, KindId kindId, String path) {
    this(hash, kindId, path, "");
  }

  public DecodeKindNodeException(Hash hash, KindId kindId, String path, String message) {
    super(buildMessage(hash, kindId, path, message));
  }

  public DecodeKindNodeException(Hash hash, KindId kindId, String path, int index, Throwable e) {
    this(hash, kindId, path + "[" + index + "]", e);
  }

  public DecodeKindNodeException(Hash hash, KindId kindId, String path, Throwable e) {
    super(buildMessage(hash, kindId, path, ""), e);
  }

  private static String buildMessage(Hash hash, KindId id, String path, String message) {
    return "Cannot decode kind " + id + " at " + hash + ". Cannot decode its node at " + "`" + path
        + "` path in Merkle tree." + (message.isEmpty() ? "" : " " + message);
  }
}
