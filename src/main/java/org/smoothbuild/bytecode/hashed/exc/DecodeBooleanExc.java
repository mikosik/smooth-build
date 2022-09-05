package org.smoothbuild.bytecode.hashed.exc;

import org.smoothbuild.bytecode.hashed.Hash;

public class DecodeBooleanExc extends HashedDbExc {
  public DecodeBooleanExc(Hash hash) {
    this(hash, null);
  }

  public DecodeBooleanExc(Hash hash, DecodeByteExc e) {
    super("Value at " + hash + " cannot be decoded as boolean.", e);
  }
}
