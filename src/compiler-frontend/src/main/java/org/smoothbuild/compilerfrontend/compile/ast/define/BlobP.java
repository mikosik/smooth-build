package org.smoothbuild.compilerfrontend.compile.ast.define;

import okio.ByteString;
import org.smoothbuild.common.base.DecodeHexException;
import org.smoothbuild.common.base.Hex;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;

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
