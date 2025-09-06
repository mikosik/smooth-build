package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import okio.ByteString;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.base.DecodeHexException;
import org.smoothbuild.common.base.Hex;
import org.smoothbuild.common.log.location.Location;

public final class PBlob extends PLiteral {
  private @Nullable ByteString byteString;

  public PBlob(String literal, Location location) {
    super(literal, location);
  }

  public void decodeByteString() throws DecodeHexException {
    byteString = Hex.decode(literal());
  }

  public ByteString byteString() {
    return checkInitializedToNotNull(byteString, "byteString");
  }
}
