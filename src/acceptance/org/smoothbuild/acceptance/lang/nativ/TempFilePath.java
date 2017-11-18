package org.smoothbuild.acceptance.lang.nativ;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.File;

import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class TempFilePath {
  @SmoothFunction
  public static SString tempFilePath(NativeApi nativeApi) {
    TempDir tempDir = nativeApi.createTempDir();
    String osPath = tempDir.asOsPath(path("file.txt"));
    new File(osPath).mkdirs();
    return nativeApi.create().string(osPath);
  }
}
