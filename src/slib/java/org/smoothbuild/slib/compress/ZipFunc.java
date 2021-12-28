package org.smoothbuild.slib.compress;

import static okio.Okio.sink;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.db.bytecode.obj.val.BlobBBuilder;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.DuplicatesDetector;

import okio.BufferedSource;

public class ZipFunc {
  public static BlobB func(NativeApi nativeApi, ArrayB files) throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    BlobBBuilder blobBuilder = nativeApi.factory().blobBuilder();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.sink().outputStream())) {
      for (TupleB file : files.elems(TupleB.class)) {
        String path = filePath(file).toJ();
        if (duplicatesDetector.addValue(path)) {
          nativeApi.log().error("Cannot zip two files with the same path = " + path);
          return null;
        }
        zipFile(file, zipOutputStream);
      }
    }
    return blobBuilder.build();
  }

  private static void zipFile(TupleB file, ZipOutputStream zipOutputStream) throws IOException {
    ZipEntry entry = new ZipEntry(filePath(file).toJ());
    zipOutputStream.putNextEntry(entry);
    try (BufferedSource source = fileContent(file).source()) {
      source.readAll(sink(zipOutputStream));
    }
    zipOutputStream.closeEntry();
  }
}
