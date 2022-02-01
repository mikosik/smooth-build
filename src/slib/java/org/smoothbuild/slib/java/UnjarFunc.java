package org.smoothbuild.slib.java;

import java.io.IOException;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.plugin.NativeApi;

import net.lingala.zip4j.exception.ZipException;

public class UnjarFunc {
  public static ArrayB func(NativeApi nativeApi, BlobB jar) throws IOException {
    try {
      return nativeApi.unzipper().unzip(jar, string -> !string.equals("META-INF/MANIFEST.MF"));
    } catch (ZipException e) {
      nativeApi.log().error(
          "Cannot read archive. Corrupted data? Internal message: " + e.getMessage());
      return null;
    }
  }
}
