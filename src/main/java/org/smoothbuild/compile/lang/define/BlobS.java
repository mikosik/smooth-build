package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.vm.execute.TaskInfo.NAME_LENGTH_LIMIT;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.BlobTS;

import okio.ByteString;

public record BlobS(BlobTS type, ByteString byteString, Loc loc) implements ValS {
  @Override
  public String label() {
    int limit = NAME_LENGTH_LIMIT;
    String string = "0x" + byteString.hex();
    if (string.length() <= limit) {
      return string;
    } else {
      return string.substring(0, limit - 3) + "...";
    }
  }
}
