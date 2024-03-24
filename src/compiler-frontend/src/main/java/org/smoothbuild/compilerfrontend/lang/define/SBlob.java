package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

import okio.ByteString;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SBlobType;

public record SBlob(SBlobType type, ByteString byteString, Location location) implements SConstant {
  @Override
  public String toString() {
    return "BlobS(" + list(type, "0x" + byteString.hex(), location).toString(", ") + ")";
  }
}
