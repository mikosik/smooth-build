package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.DecodeHexException;
import org.smoothbuild.util.Hex;

import okio.ByteString;

public final class BlobN extends ExprN {
  private final String literal;
  private ByteString byteString;

  public BlobN(String literal, Location location) {
    super(location);
    this.literal = literal;
  }

  public String literal() {
    return literal;
  }

  public void decodeByteString() throws DecodeHexException {
    byteString = Hex.decode(literal);
  }

  public ByteString byteString() {
    return byteString;
  }
}