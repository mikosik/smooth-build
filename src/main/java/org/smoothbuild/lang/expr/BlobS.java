package org.smoothbuild.lang.expr;

import static org.smoothbuild.vm.job.job.TaskInfo.NAME_LENGTH_LIMIT;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.impl.BlobTS;

import okio.ByteString;

public record BlobS(BlobTS type, ByteString byteString, Loc loc) implements ExprS {
  @Override
  public String name() {
    int limit = NAME_LENGTH_LIMIT;
    String string = "0x" + byteString.hex();
    if (string.length() <= limit) {
      return string;
    } else {
      return string.substring(0, limit - 3) + "...";
    }
  }
}
