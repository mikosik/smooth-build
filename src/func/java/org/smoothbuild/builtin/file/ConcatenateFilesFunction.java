package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ConcatenateFilesFunction {
  public interface ConcatenateFilesParameters {
    @Required
    public Array<SFile> files();

    @Required
    public Array<SFile> with();
  }

  @SmoothFunction
  public static Array<SFile> concatenateFiles(NativeApi nativeApi, ConcatenateFilesParameters params) {
    ArrayBuilder<SFile> builder = nativeApi.arrayBuilder(SFile.class);

    for (SFile file : params.files()) {
      builder.add(file);
    }
    for (SFile file : params.with()) {
      builder.add(file);
    }

    return builder.build();
  }
}
