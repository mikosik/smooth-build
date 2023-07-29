package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.common.DecodeHexExc;
import org.smoothbuild.common.Hex;
import org.smoothbuild.compile.fs.lang.base.location.Location;

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
