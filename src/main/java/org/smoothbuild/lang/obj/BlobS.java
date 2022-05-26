package org.smoothbuild.lang.obj;

import static org.smoothbuild.vm.job.TaskInfo.NAME_LENGTH_LIMIT;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.BlobTS;

import okio.ByteString;

public record BlobS(BlobTS type, ByteString byteString, Loc loc) implements CnstS {
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
