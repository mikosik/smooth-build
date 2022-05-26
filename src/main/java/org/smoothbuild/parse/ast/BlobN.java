package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.Hex;

import okio.ByteString;

public final class BlobN extends CnstN {
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
