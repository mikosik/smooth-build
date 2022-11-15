package org.smoothbuild.slib.compress;

import static okio.Okio.sink;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.BlobBBuilder;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;

public class ZipFunc {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws IOException {
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
        addZipEntry(zipOutputStream, file);
      }
    }
    return blobBuilder.build();
  }

  private static void addZipEntry(ZipOutputStream zipOutputStream, TupleB file) throws IOException {
    var zipEntry = new ZipEntry(filePath(file).toJ());
    zipEntry.setLastModifiedTime(FileTime.fromMillis(0));
    zipOutputStream.putNextEntry(zipEntry);
    try (BufferedSource source = fileContent(file).source()) {
      source.readAll(sink(zipOutputStream));
    }
    zipOutputStream.closeEntry();
  }
}
