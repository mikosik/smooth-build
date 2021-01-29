package org.smoothbuild.acceptance.testing;

import java.io.File;
import java.io.IOException;

import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.plugin.NativeApi;

public class TempFilePath {
  public static Str function(NativeApi nativeApi) throws IOException {
    TempDir tempDir = nativeApi.createTempDir();
    String osPath = tempDir.rootOsPath() + "/file.txt";
    new File(osPath).mkdirs();
    return nativeApi.factory().string(osPath);
  }
}
