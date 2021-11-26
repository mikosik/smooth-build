package org.smoothbuild.lang.expr;

import static org.smoothbuild.exec.job.TaskInfo.NAME_LENGTH_LIMIT;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.BlobTypeS;

import okio.ByteString;

public record BlobS(BlobTypeS type, ByteString byteString, Location location) implements LiteralS {
  @Override
  public String name() {
    return toShortString();
  }

  @Override
  public String toShortString() {
    int limit = NAME_LENGTH_LIMIT;
    String string = "0x" + byteString.hex();
    if (string.length() <= limit) {
      return string;
    } else {
      return string.substring(0, limit - 3) + "...";
    }
  }
}
