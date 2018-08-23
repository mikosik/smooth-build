package org.smoothbuild.builtin.blob;

import java.io.IOException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.Streams;

public class ToStringFunction {
  @SmoothFunction
  public static SString toString(NativeApi nativeApi, Blob blob) throws IOException {
    String string = Streams.inputStreamToString(blob.openInputStream());
    return nativeApi.create().string(string);
  }
}
