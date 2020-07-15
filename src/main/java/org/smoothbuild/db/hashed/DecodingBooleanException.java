package org.smoothbuild.db.hashed;

public class DecodingBooleanException extends HashedDbException {
  public DecodingBooleanException(Hash hash) {
    super(hash, null);
  }

  public DecodingBooleanException(Hash hash, DecodingByteException e) {
    super("Value at " + hash + " cannot be decoded as boolean.", e);
  }
}
