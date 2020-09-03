package org.smoothbuild.slib.compress;

import static okio.Okio.sink;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.BlobBuilder;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;
import org.smoothbuild.util.DuplicatesDetector;

import okio.BufferedSource;

public class ZipFunction {
  @NativeImplementation("zip")
  public static Blob zip(NativeApi nativeApi, Array files) throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    BlobBuilder blobBuilder = nativeApi.factory().blobBuilder();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.sink().outputStream())) {
      for (Tuple file : files.asIterable(Tuple.class)) {
        String path = filePath(file).jValue();
        if (duplicatesDetector.addValue(path)) {
          nativeApi.log().error("Cannot zip two files with the same path = " + path);
          return null;
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
