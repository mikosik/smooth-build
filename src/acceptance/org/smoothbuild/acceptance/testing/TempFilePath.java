package org.smoothbuild.acceptance.testing;

import java.io.File;
import java.io.IOException;

import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.SString;

public class TempFilePath {
  @SmoothFunction("tempFilePath")
  public static SString tempFilePath(NativeApi nativeApi) throws IOException {
    TempDir tempDir = nativeApi.createTempDir();
    String osPath = tempDir.rootOsPath() + "/file.txt";
    new File(osPath).mkdirs();
    return nativeApi.factory().string(osPath);
  }
}
