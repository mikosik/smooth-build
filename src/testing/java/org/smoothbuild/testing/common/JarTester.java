package org.smoothbuild.testing.common;

import static okio.Okio.sink;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;

import okio.Buffer;
import okio.BufferedSource;

public class JarTester {

  public static Blob jar(Struct... files) throws IOException {
    Buffer buffer = new Buffer();
    try (JarOutputStream jarOutputStream = new JarOutputStream(buffer.outputStream())) {
      for (Struct file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    return new TestingContext().blob(buffer.readByteString());
  }

  private static void addEntry(JarOutputStream jarOutputStream, Struct file) throws IOException {
    JarEntry entry = new JarEntry(((SString) file.get("path")).jValue());
    jarOutputStream.putNextEntry(entry);
    try (BufferedSource source = ((Blob) file.get("content")).source()) {
      source.readAll(sink(jarOutputStream));
    }
    jarOutputStream.closeEntry();
  }
}
