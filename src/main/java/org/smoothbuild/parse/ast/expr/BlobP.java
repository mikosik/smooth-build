package org.smoothbuild.parse.ast.expr;

import static org.smoothbuild.lang.type.TypeFS.BLOB;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.Hex;

import okio.ByteString;

public final class BlobP extends ValP {
  private final String literal;
  private ByteString byteString;

  public BlobP(String literal, Loc loc) {
    super(BLOB, loc);
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
