package org.smoothbuild.builtin.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ToBlobFunction {
  @SmoothFunction("toBlob")
  public static Blob toBlob(NativeApi nativeApi, SString string) throws IOException {
    return stringToBlob(nativeApi, string);
  }

  public static Blob stringToBlob(NativeApi nativeApi, SString string) throws IOException {
    return nativeApi.factory().blob(sink -> sink.writeString(string.data(), CHARSET));
  }
}
