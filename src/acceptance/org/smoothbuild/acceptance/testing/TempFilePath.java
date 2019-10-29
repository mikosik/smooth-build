package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.File;
import java.io.IOException;

import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class TempFilePath {
  @SmoothFunction("tempFilePath")
  public static SString tempFilePath(NativeApi nativeApi) throws IOException {
    TempDir tempDir = nativeApi.createTempDir();
    String osPath = tempDir.asOsPath(path("file.txt"));
    new File(osPath).mkdirs();
    return nativeApi.factory().string(osPath);
  }
}
