package org.smoothbuild.acceptance.testing;

import java.io.File;
import java.io.IOException;

import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class TempFilePath {
  @NativeImplementation("tempFilePath")
  public static RString tempFilePath(NativeApi nativeApi) throws IOException {
    TempDir tempDir = nativeApi.createTempDir();
    String osPath = tempDir.rootOsPath() + "/file.txt";
    new File(osPath).mkdirs();
    return nativeApi.factory().string(osPath);
  }
}
