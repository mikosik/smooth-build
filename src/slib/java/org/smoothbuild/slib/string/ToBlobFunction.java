package org.smoothbuild.slib.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class ToBlobFunction {
  @NativeImplementation("toBlob")
  public static Blob toBlob(NativeApi nativeApi, Str string) throws IOException {
    return stringToBlob(nativeApi, string);
  }

  public static Blob stringToBlob(NativeApi nativeApi, Str string) throws IOException {
    return nativeApi.factory().blob(sink -> sink.writeString(string.jValue(), CHARSET));
  }
}
