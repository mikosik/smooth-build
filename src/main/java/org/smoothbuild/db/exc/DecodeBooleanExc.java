package org.smoothbuild.db.exc;

import org.smoothbuild.db.Hash;

public class DecodeBooleanExc extends HashedDbExc {
  public DecodeBooleanExc(Hash hash) {
    this(hash, null);
  }

  public DecodeBooleanExc(Hash hash, DecodeByteExc e) {
    super("Value at " + hash + " cannot be decoded as boolean.", e);
  }
}
