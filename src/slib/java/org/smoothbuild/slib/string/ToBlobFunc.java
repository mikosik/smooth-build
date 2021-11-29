package org.smoothbuild.slib.string;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.plugin.NativeApi;

public class ToBlobFunc {
  public static BlobH func(NativeApi nativeApi, StringH string) throws IOException {
    return nativeApi.factory().blob(sink -> sink.writeString(string.toJ(), CHARSET));
  }
}
