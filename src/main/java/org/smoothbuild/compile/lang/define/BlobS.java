package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.BlobTS;

import okio.ByteString;

public record BlobS(BlobTS type, ByteString byteString, Location location) implements ConstantS {
  @Override
  public String toString() {
    return "BlobS(" + joinToString(", ", type, "0x" + byteString.hex(), location) + ")";
  }
}
