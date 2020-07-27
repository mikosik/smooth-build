package org.smoothbuild.slib.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.RString;

public class ToBlobFunction {
  @SmoothFunction("toBlob")
  public static Blob toBlob(NativeApi nativeApi, RString string) throws IOException {
    return stringToBlob(nativeApi, string);
  }

  public static Blob stringToBlob(NativeApi nativeApi, RString string) throws IOException {
    return nativeApi.factory().blob(sink -> sink.writeString(string.jValue(), CHARSET));
  }
}
