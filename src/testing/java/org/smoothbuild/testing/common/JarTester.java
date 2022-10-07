package org.smoothbuild.testing.common;

import static okio.Okio.sink;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.testing.TestContext;

import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

public class JarTester {
  public static BlobB jar(TupleB... files) throws IOException {
    return new TestContext().blobB(jarByteString(files));
  }

  public static ByteString jarByteString(TupleB... files) throws IOException {
    Buffer buffer = new Buffer();
    try (JarOutputStream jarOutputStream = new JarOutputStream(buffer.outputStream())) {
      for (TupleB file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    return buffer.readByteString();
  }

  private static void addEntry(JarOutputStream jarOutputStream, TupleB file) throws IOException {
    JarEntry entry = new JarEntry(filePath(file).toJ());
    jarOutputStream.putNextEntry(entry);
    try (BufferedSource source = fileContent(file).source()) {
      source.readAll(sink(jarOutputStream));
    }
    jarOutputStream.closeEntry();
  }
}
