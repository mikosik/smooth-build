package org.smoothbuild.builtin.blob;

import java.io.IOException;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.util.Streams;

public class ToStringFunction {
  public static SString execute(NativeApiImpl nativeApi,
      BuiltinSmoothModule.ToStringParameters params) {
    try {
      String string = Streams.inputStreamToString(params.blob().openInputStream());
      return nativeApi.string(string);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
