package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SFile;

public class ConcatenateFilesFunction {
  @SmoothFunction
  public static Array<SFile> concatenateFiles( //
      NativeApi nativeApi, //
      @Required @Name("files") Array<SFile> files, //
      @Required @Name("with") Array<SFile> with) {
    ArrayBuilder<SFile> builder = nativeApi.arrayBuilder(SFile.class);

    for (SFile file : files) {
      builder.add(file);
    }
    for (SFile file : with) {
      builder.add(file);
    }

    return builder.build();
  }
}
