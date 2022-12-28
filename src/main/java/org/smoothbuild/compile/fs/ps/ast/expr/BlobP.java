package org.smoothbuild.compile.fs.ps.ast.expr;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.util.DecodeHexExc;
import org.smoothbuild.util.Hex;

import okio.ByteString;

public final class BlobP extends LiteralP {
  private ByteString byteString;

  public BlobP(String literal, Location location) {
    super(literal, location);
  }

  public void decodeByteString() throws DecodeHexExc {
    byteString = Hex.decode(literal());
  }

  public ByteString byteString() {
    return byteString;
  }
}
