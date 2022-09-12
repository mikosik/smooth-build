package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.vm.execute.TaskInfo.NAME_LENGTH_LIMIT;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.BlobTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

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

  @Override
  public BlobS mapVars(Function<VarS, TypeS> mapper) {
    return this;
  }
}
