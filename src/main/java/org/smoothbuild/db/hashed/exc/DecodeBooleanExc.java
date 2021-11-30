package org.smoothbuild.db.hashed.exc;

import org.smoothbuild.db.hashed.Hash;

public class DecodeBooleanExc extends HashedDbExc {
  public DecodeBooleanExc(Hash hash) {
    this(hash, null);
  }

  public DecodeBooleanExc(Hash hash, DecodeByteExc e) {
    super("Value at " + hash + " cannot be decoded as boolean.", e);
  }
}
