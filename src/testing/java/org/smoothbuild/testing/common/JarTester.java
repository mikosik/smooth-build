package org.smoothbuild.testing.common;

import static okio.Okio.sink;
import static org.smoothbuild.record.db.FileStruct.fileContent;
import static org.smoothbuild.record.db.FileStruct.filePath;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.testing.TestingContext;

import okio.Buffer;
import okio.BufferedSource;

public class JarTester {

  public static Blob jar(Tuple... files) throws IOException {
    Buffer buffer = new Buffer();
    try (JarOutputStream jarOutputStream = new JarOutputStream(buffer.outputStream())) {
      for (Tuple file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    return new TestingContext().blob(buffer.readByteString());
  }

  private static void addEntry(JarOutputStream jarOutputStream, Tuple file) throws IOException {
    JarEntry entry = new JarEntry(filePath(file).jValue());
    jarOutputStream.putNextEntry(entry);
    try (BufferedSource source = fileContent(file).source()) {
      source.readAll(sink(jarOutputStream));
    }
    jarOutputStream.closeEntry();
  }
}
