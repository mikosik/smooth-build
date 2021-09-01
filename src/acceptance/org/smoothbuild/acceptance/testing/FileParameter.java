package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.plugin.NativeApi;

public class FileParameter {
  public static Rec function(NativeApi nativeApi, Rec file) {
    return file;
  }
}
