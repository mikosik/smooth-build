package org.smoothbuild.builtin.blob;

import java.io.IOException;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.Streams;

public class ToStringFunction {
  @SmoothFunction
  public static SString toString(NativeApi nativeApi, Blob blob) {
    try {
      String string = Streams.inputStreamToString(blob.openInputStream());
      return nativeApi.create().string(string);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
