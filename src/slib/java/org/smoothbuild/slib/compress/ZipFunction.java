package org.smoothbuild.slib.compress;

import static okio.Okio.sink;
import static org.smoothbuild.record.db.FileStruct.fileContent;
import static org.smoothbuild.record.db.FileStruct.filePath;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.BlobBuilder;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.util.DuplicatesDetector;

import okio.BufferedSource;

public class ZipFunction {
  @SmoothFunction("zip")
  public static Blob zip(NativeApi nativeApi, Array files) throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    BlobBuilder blobBuilder = nativeApi.factory().blobBuilder();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.sink().outputStream())) {
      for (Tuple file : files.asIterable(Tuple.class)) {
        String path = filePath(file).jValue();
        if (duplicatesDetector.addValue(path)) {
          nativeApi.log().error("Cannot zip two files with the same path = " + path);
          throw new AbortException();
        }
        zipFile(file, zipOutputStream);
      }
    }
    return blobBuilder.build();
  }

  private static void zipFile(Tuple file, ZipOutputStream zipOutputStream)
      throws IOException {
    ZipEntry entry = new ZipEntry(filePath(file).jValue());
    zipOutputStream.putNextEntry(entry);
    try (BufferedSource source = fileContent(file).source()) {
      source.readAll(sink(zipOutputStream));
    }
    zipOutputStream.closeEntry();
  }
}
