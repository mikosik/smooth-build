package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.Hex;

import okio.ByteString;

public final class BlobP extends CnstP {
  private final String literal;
  private ByteString byteString;

  public BlobP(String literal, Loc loc) {
    super(TypeFS.blob(), loc);
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
