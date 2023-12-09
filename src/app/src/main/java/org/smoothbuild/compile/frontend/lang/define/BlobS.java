package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

import okio.ByteString;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.BlobTS;

public record BlobS(BlobTS type, ByteString byteString, Location location) implements ConstantS {
  @Override
  public String toString() {
    return "BlobS(" + list(type, "0x" + byteString.hex(), location).toString(", ") + ")";
  }
}
