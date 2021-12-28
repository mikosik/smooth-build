package org.smoothbuild.slib.java;

import static org.smoothbuild.slib.compress.UnzipFunc.unzip;

import java.io.IOException;
import java.util.zip.ZipException;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.plugin.NativeApi;

public class UnjarFunc {
  public static ArrayB func(NativeApi nativeApi, BlobB jar) throws IOException {
    try {
      return unzip(nativeApi, jar, string -> !string.equals("META-INF/MANIFEST.MF"));
    } catch (ZipException e) {
      nativeApi.log().error("Cannot read archive. Corrupted data?");
      return null;
    }
  }
}
