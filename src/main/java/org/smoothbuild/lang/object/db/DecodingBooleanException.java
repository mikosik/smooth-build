package org.smoothbuild.lang.object.db;

import org.smoothbuild.db.hashed.Hash;

public class DecodingBooleanException extends ValuesDbException {
  public DecodingBooleanException(Hash hash) {
    super("Value at " + hash + " cannot be decoded as boolean.");
  }
}
