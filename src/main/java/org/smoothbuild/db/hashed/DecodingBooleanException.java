package org.smoothbuild.db.hashed;

public class DecodingBooleanException extends HashedDbException {
  public DecodingBooleanException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as boolean.");
  }
}
