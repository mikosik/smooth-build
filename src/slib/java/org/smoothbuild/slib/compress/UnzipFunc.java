package org.smoothbuild.slib.compress;

import java.io.IOException;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.plugin.NativeApi;

import net.lingala.zip4j.exception.ZipException;

public class UnzipFunc {
  public static ArrayB func(NativeApi nativeApi, BlobB blob) throws IOException {
    try {
      return nativeApi.unzipper().unzip(blob, x -> true);
    } catch (ZipException e) {
      nativeApi.log().error(
          "Cannot read archive. Corrupted data? Internal message: " + e.getMessage());
      return null;
    }
  }
}
