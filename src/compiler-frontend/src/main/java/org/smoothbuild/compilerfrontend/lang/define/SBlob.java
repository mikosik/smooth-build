package org.smoothbuild.compilerfrontend.lang.define;

import okio.ByteString;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SBlobType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;

public record SBlob(SBlobType type, ByteString byteString, Location location) implements SConstant {
  @Override
  public String toSourceCode(Collection<STypeVar> localTypeVars) {
    return "0x" + byteString.hex();
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SBlob")
        .addField("type", type)
        .addField("byteString", "0x" + byteString.hex())
        .addField("location", location)
        .toString();
  }
}
