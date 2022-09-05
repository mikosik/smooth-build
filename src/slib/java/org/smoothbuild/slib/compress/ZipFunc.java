package org.smoothbuild.slib.compress;

import static okio.Okio.sink;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;

import java.io.IOException;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.BlobBBuilder;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;

public class ZipFunc {
  public static CnstB func(NativeApi nativeApi, TupleB args) throws IOException {
    ArrayB files = (ArrayB) args.get(0);
    var duplicatesDetector = new HashSet<String>();
    BlobBBuilder blobBuilder = nativeApi.factory().blobBuilder();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.sink().outputStream())) {
      for (TupleB file : files.elems(TupleB.class)) {
        String path = filePath(file).toJ();
        if (!duplicatesDetector.add(path)) {
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
