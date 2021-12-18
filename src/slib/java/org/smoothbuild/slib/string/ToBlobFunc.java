package org.smoothbuild.slib.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.plugin.NativeApi;

public class ToBlobFunc {
  public static BlobB func(NativeApi nativeApi, StringB string) throws IOException {
    return nativeApi.factory().blob(sink -> sink.writeString(string.toJ(), CHARSET));
  }
}
