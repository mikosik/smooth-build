package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class FileArrayToBlobArrayFunction {
  public interface Parameters {
    @Required
    public Array<SFile> input();
  }

  @SmoothFunction
  public static Array<Blob> fileArrayToBlobArray(NativeApi nativeApi, Parameters params) {
    ArrayBuilder<Blob> builder = nativeApi.arrayBuilder(Blob.class);
    for (SFile file : params.input()) {
      builder.add(file.content());
    }
    return builder.build();
  }
}
