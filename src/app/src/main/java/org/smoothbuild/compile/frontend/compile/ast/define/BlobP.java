package org.smoothbuild.compile.frontend.compile.ast.define;

import org.smoothbuild.common.DecodeHexException;
import org.smoothbuild.common.Hex;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

import okio.ByteString;

public final class BlobP extends LiteralP {
  private ByteString byteString;

  public BlobP(String literal, Location location) {
    super(literal, location);
  }

  public void decodeByteString() throws DecodeHexException {
    byteString = Hex.decode(literal());
  }

  public ByteString byteString() {
    return byteString;
  }
}
