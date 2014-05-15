package org.smoothbuild.builtin.file;

import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;

public class ConcatenateFilesFunction {

  public static SArray<SFile> execute(NativeApi nativeApi,
      BuiltinSmoothModule.ConcatenateFilesParameters params) {
    ArrayBuilder<SFile> builder = nativeApi.arrayBuilder(FILE_ARRAY);

    for (SFile file : params.files()) {
      builder.add(file);
    }
    for (SFile file : params.with()) {
      builder.add(file);
    }

    return builder.build();
  }
}
