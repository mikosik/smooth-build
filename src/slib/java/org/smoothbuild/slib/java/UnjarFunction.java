package org.smoothbuild.slib.java;

import static org.smoothbuild.slib.compress.UnzipFunction.unzip;

import java.io.IOException;
import java.util.zip.ZipException;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.plugin.NativeApi;

public class UnjarFunction {
  public static Array function(NativeApi nativeApi, Blob jar) throws IOException {
    try {
      return unzip(nativeApi, jar, string -> !string.equals("META-INF/MANIFEST.MF"));
    } catch (ZipException e) {
      nativeApi.log().error("Cannot read archive. Corrupted data?");
      return null;
    }
  }
}
