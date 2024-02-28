package org.smoothbuild.vm.bytecode.type.exc;

import org.smoothbuild.common.Hash;

public class DecodeCatException extends CategoryDbException {
  public DecodeCatException(Hash hash) {
    this(hash, null, null);
  }

  public DecodeCatException(Hash hash, Exception cause) {
    this(hash, null, cause);
  }

  public DecodeCatException(String message) {
    super(message);
  }

  public DecodeCatException(String message, Throwable e) {
    super(message, e);
  }

  public DecodeCatException(Hash hash, String message) {
    this(hash, message, null);
  }

  public DecodeCatException(Hash hash, String message, Throwable cause) {
    super(buildMessage(hash, message), cause);
  }

  private static String buildMessage(Hash hash, String message) {
    return "Cannot decode category at " + hash + "." + (message == null ? "" : " " + message);
  }
}
