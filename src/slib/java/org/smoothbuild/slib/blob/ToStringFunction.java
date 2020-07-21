package org.smoothbuild.slib.blob;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.SString;

import okio.BufferedSource;

public class ToStringFunction {
  @SmoothFunction("toString")
  public static SString toString(NativeApi nativeApi, Blob blob) throws IOException {
    try (BufferedSource source = blob.source()) {
      return nativeApi.factory().string(source.readString(CHARSET));
    }
  }
}
