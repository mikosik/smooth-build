package org.smoothbuild.builtin.compress;

import static okio.Okio.sink;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.util.DuplicatesDetector;

import okio.BufferedSource;

public class ZipFunction {
  @SmoothFunction("zip")
  public static Blob zip(NativeApi nativeApi, Array files, Array javaHash) throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    BlobBuilder blobBuilder = nativeApi.factory().blobBuilder();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.sink().outputStream())) {
      for (Struct file : files.asIterable(Struct.class)) {
        String path = ((SString) file.get("path")).data();
        if (duplicatesDetector.addValue(path)) {
          nativeApi.log().error("Cannot zip two files with the same path = " + path);
          throw new AbortException();
        }
        zipFile(file, zipOutputStream);
      }
    }
    return blobBuilder.build();
  }

  private static void zipFile(Struct file, ZipOutputStream zipOutputStream)
      throws IOException {
    ZipEntry entry = new ZipEntry(((SString) file.get("path")).data());
    zipOutputStream.putNextEntry(entry);
    try (BufferedSource source = ((Blob) file.get("content")).source()) {
      source.readAll(sink(zipOutputStream));
    }
    zipOutputStream.closeEntry();
  }
}
