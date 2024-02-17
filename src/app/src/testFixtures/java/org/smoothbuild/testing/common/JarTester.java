package org.smoothbuild.testing.common;

import static okio.Okio.sink;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;

import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class JarTester {
  public static BlobB jar(TupleB... files) throws Exception {
    return new TestContext().blobB(jarByteString(files));
  }

  public static ByteString jarByteString(TupleB... files) throws Exception {
    Buffer buffer = new Buffer();
    try (JarOutputStream jarOutputStream = new JarOutputStream(buffer.outputStream())) {
      for (TupleB file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    return buffer.readByteString();
  }

  private static void addEntry(JarOutputStream jarOutputStream, TupleB file) throws Exception {
    JarEntry entry = new JarEntry(filePath(file).toJ());
    jarOutputStream.putNextEntry(entry);
    try (BufferedSource source = fileContent(file).source()) {
      var sink = sink(jarOutputStream);
      source.readAll(sink);
      sink.flush();
    }
    jarOutputStream.closeEntry();
  }
}
