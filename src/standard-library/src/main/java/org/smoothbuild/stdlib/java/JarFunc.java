package org.smoothbuild.stdlib.java;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class JarFunc {
  private static final String MANIFEST_FILE_PATH = "META-INF/MANIFEST.MF";

  public static BValue func(NativeApi nativeApi, BTuple args) throws IOException {
    BArray files = (BArray) args.get(0);
    BBlob manifest = (BBlob) args.get(1);

    var duplicatesDetector = new HashSet<String>();
    try (var blobBuilder = nativeApi.factory().blobBuilder()) {
      try (var jarOutputStream = new JarOutputStream(buffer(blobBuilder).outputStream())) {
        for (BTuple file : files.elements(BTuple.class)) {
          var filePath = filePath(file).toJavaString();
          if (!duplicatesDetector.add(filePath)) {
            nativeApi.log().error("Cannot jar two files with the same path = " + filePath);
            return null;
          }
          addJarEntry(jarOutputStream, filePath(file).toJavaString(), fileContent(file));
        }
        addJarEntry(jarOutputStream, MANIFEST_FILE_PATH, manifest);
      }
      return blobBuilder.build();
    }
  }

  private static void addJarEntry(
      JarOutputStream jarOutputStream, String filePath, BBlob fileContent) throws IOException {
    var jarEntry = new JarEntry(filePath);
    jarEntry.setLastModifiedTime(FileTime.fromMillis(0));
    jarOutputStream.putNextEntry(jarEntry);
    try (var source = buffer(fileContent.source())) {
      var sink = sink(jarOutputStream);
      source.readAll(sink);
      sink.flush();
    }
    jarOutputStream.closeEntry();
  }
}
