package org.smoothbuild.virtualmachine.bytecode.hashed.exc;

import org.smoothbuild.common.base.Hash;

public class DecodeBooleanException extends HashedDbException {
  public DecodeBooleanException(Hash hash) {
    this(hash, null);
  }

  public DecodeBooleanException(Hash hash, DecodeByteException e) {
    super("Value at " + hash + " cannot be decoded as boolean.", e);
  }
}
