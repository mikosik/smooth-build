package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class FileArrayToBlobArrayFunction {
  public interface Parameters {
    @Required
    public Array<SFile> input();
  }

  @SmoothFunction
  public static Array<Blob> fileArrayToBlobArray(NativeApi nativeApi, Parameters params) {
    ArrayBuilder<Blob> builder = nativeApi.arrayBuilder(BLOB_ARRAY);
    for (SFile file : params.input()) {
      builder.add(file.content());
    }
    return builder.build();
  }
}
