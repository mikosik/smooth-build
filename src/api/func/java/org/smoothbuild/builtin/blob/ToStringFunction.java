package org.smoothbuild.builtin.blob;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;

import okio.BufferedSource;

public class ToStringFunction {
  @SmoothFunction("toString")
  public static SString toString(NativeApi nativeApi, Blob blob) throws IOException {
    try (BufferedSource source = blob.source()) {
      return nativeApi.create().string(source.readString(CHARSET));
    }
  }
}
