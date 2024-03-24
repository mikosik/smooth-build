package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeKindException extends BKindDbException {
  public DecodeKindException(Hash hash) {
    this(hash, null, null);
  }

  public DecodeKindException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public DecodeKindException(String message) {
    super(message);
  }

  public DecodeKindException(String message, Throwable e) {
    super(message, e);
  }

  public DecodeKindException(Hash hash, String message) {
    this(hash, message, null);
  }

  public DecodeKindException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot decode kind at " + hash + "." + (message == null ? "" : " " + message);
  }
}
