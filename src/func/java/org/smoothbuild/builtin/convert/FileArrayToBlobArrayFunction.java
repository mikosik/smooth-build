package org.smoothbuild.builtin.convert;

import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;

import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class FileArrayToBlobArrayFunction {
  public interface Parameters {
    @Required
    public SArray<SFile> input();
  }

  @SmoothFunction(name = "fileArrayToBlobArray")
  public static SArray<SBlob> execute(NativeApi nativeApi, Parameters params) {
    ArrayBuilder<SBlob> builder = nativeApi.arrayBuilder(BLOB_ARRAY);
    for (SFile file : params.input()) {
      builder.add(file.content());
    }
    return builder.build();
  }
}
