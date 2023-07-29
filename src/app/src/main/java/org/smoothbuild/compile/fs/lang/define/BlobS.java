package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.BlobTS;

import okio.ByteString;

public record BlobS(BlobTS type, ByteString byteString, Location location) implements ConstantS {
  @Override
  public String toString() {
    return "BlobS(" + joinToString(", ", type, "0x" + byteString.hex(), location) + ")";
  }
}
