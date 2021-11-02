package org.smoothbuild.testing.common;

import static okio.Okio.sink;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.testing.TestingContextImpl;

import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

public class JarTester {
  public static Blob jar(Struc_... files) throws IOException {
    return new TestingContextImpl().blob(jarByteString(files));
  }

  public static ByteString jarByteString(Struc_... files) throws IOException {
    Buffer buffer = new Buffer();
    try (JarOutputStream jarOutputStream = new JarOutputStream(buffer.outputStream())) {
      for (Struc_ file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    ByteString bytes = buffer.readByteString();
    return bytes;
  }

  private static void addEntry(JarOutputStream jarOutputStream, Struc_ file) throws IOException {
    JarEntry entry = new JarEntry(filePath(file).jValue());
    jarOutputStream.putNextEntry(entry);
    try (BufferedSource source = fileContent(file).source()) {
      source.readAll(sink(jarOutputStream));
    }
    jarOutputStream.closeEntry();
  }
}
