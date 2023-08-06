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
import okio.BufferedSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class JarFunc {
  private static final String MANIFEST_FILE_PATH = "META-INF/MANIFEST.MF";

  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    ArrayB files = (ArrayB) args.get(0);
    BlobB manifest = (BlobB) args.get(1);

    var duplicatesDetector = new HashSet<String>();
    try (var blobBuilder = nativeApi.factory().blobBuilder()) {
      try (var jarOutputStream = new JarOutputStream(buffer(blobBuilder).outputStream())) {
        for (TupleB file : files.elems(TupleB.class)) {
          var filePath = filePath(file).toJ();
          if (!duplicatesDetector.add(filePath)) {
            nativeApi.log().error("Cannot jar two files with the same path = " + filePath);
            return null;
          }
          addJarEntry(jarOutputStream, filePath(file).toJ(), fileContent(file));
        }
        addJarEntry(jarOutputStream, MANIFEST_FILE_PATH, manifest);
      }
      return blobBuilder.build();
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  private static void addJarEntry(
      JarOutputStream jarOutputStream, String filePath, BlobB fileContent)
      throws IOException, BytecodeException {
    var jarEntry = new JarEntry(filePath);
    jarEntry.setLastModifiedTime(FileTime.fromMillis(0));
    jarOutputStream.putNextEntry(jarEntry);
    try (BufferedSource source = fileContent.source()) {
      var sink = sink(jarOutputStream);
      source.readAll(sink);
      sink.flush();
    }
    jarOutputStream.closeEntry();
  }
}
