package org.smoothbuild.lang.define;

import static org.smoothbuild.vm.job.TaskInfo.NAME_LENGTH_LIMIT;

import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.BlobTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

import okio.ByteString;

public record BlobS(BlobTS type, ByteString byteString, Loc loc) implements ValS {
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

  @Override
  public BlobS mapVars(Function<VarS, TypeS> mapper) {
    return this;
  }
}
