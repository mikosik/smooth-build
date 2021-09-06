package org.smoothbuild.db.hashed;

public class DecodeBooleanException extends HashedDbException {
  public DecodeBooleanException(Hash hash) {
    this(hash, null);
  }

  public DecodeBooleanException(Hash hash, DecodeByteException e) {
    super("Value at " + hash + " cannot be decoded as boolean.", e);
  }
}