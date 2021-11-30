package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.Hex;

import okio.ByteString;

public final class BlobN extends ExprN {
  private final String literal;
  private ByteString byteString;

  public BlobN(String literal, Loc loc) {
    super(loc);
    this.literal = literal;
  }

  public String literal() {
    return literal;
  }

  public void decodeByteString() throws DecodeHexExc {
    byteString = Hex.decode(literal);
  }

  public ByteString byteString() {
    return byteString;
  }
}
