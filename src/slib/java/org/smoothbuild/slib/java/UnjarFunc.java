package org.smoothbuild.slib.java;

import static org.smoothbuild.slib.compress.UnzipFunc.unzip;

import java.io.IOException;
import java.util.zip.ZipException;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.plugin.NativeApi;

public class UnjarFunc {
  public static ArrayH func(NativeApi nativeApi, BlobH jar) throws IOException {
    try {
      return unzip(nativeApi, jar, string -> !string.equals("META-INF/MANIFEST.MF"));
    } catch (ZipException e) {
      nativeApi.log().error("Cannot read archive. Corrupted data?");
      return null;
    }
  }
}
