package org.smoothbuild.stdlib.compress;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ZipFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws IOException {
    BArray files = (BArray) args.get(0);
    var duplicatesDetector = new HashSet<String>();
    try (var blobBuilder = nativeApi.factory().blobBuilder()) {
      try (var zipOutputStream = new ZipOutputStream(buffer(blobBuilder).outputStream())) {
        for (BTuple file : files.elements(BTuple.class)) {
          String path = filePath(file).toJavaString();
          if (!duplicatesDetector.add(path)) {
            nativeApi.log().error("Cannot zip two files with the same path = " + path);
            return null;
          }
          addZipEntry(zipOutputStream, file);
        }
      }
      return blobBuilder.build();
    }
  }

  private static void addZipEntry(ZipOutputStream zipOutputStream, BTuple file) throws IOException {
    var zipEntry = new ZipEntry(filePath(file).toJavaString());
    zipEntry.setLastModifiedTime(FileTime.fromMillis(0));
    zipOutputStream.putNextEntry(zipEntry);
    try (var source = buffer(fileContent(file).source())) {
      source.readAll(sink(zipOutputStream));
    }
    zipOutputStream.closeEntry();
  }
}
