package org.smoothbuild.slib.blob;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

import okio.BufferedSource;

public class ToStringFunction {
  @NativeImplementation("toString")
  public static Str toString(NativeApi nativeApi, Blob blob) throws IOException {
    try (BufferedSource source = blob.source()) {
      return nativeApi.factory().string(source.readString(CHARSET));
    }
  }
}
