package org.smoothbuild.virtualmachine.bytecode.hashed.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeBooleanException extends HashedDbException {
  public DecodeBooleanException(Hash hash) {
    super(message(hash));
  }

  public DecodeBooleanException(Hash hash, DecodeByteException e) {
    super(message(hash), e);
  }

  private static String message(Hash hash) {
    return "Value at " + hash + " cannot be decoded as boolean.";
  }
}
