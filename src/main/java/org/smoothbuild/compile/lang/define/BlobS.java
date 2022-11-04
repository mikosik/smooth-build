package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.BlobTS;

import okio.ByteString;

public record BlobS(BlobTS type, ByteString byteString, Loc loc) implements InstS {
  @Override
  public String toString() {
    return "BlobS(" + joinToString(", ", type, "0x" + byteString.hex(), loc) + ")";
  }
}
