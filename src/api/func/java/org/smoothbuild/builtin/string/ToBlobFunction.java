package org.smoothbuild.builtin.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;

public class ToBlobFunction {
  @SmoothFunction
  public static Blob toBlob(NativeApi nativeApi, SString string) throws IOException {
    return stringToBlob(nativeApi, string);
  }

  public static Blob stringToBlob(NativeApi nativeApi, SString string) throws IOException {
    BlobBuilder builder = nativeApi.create().blobBuilder();
    builder.sink().writeString(string.data(), CHARSET);
    return builder.build();
  }
}
