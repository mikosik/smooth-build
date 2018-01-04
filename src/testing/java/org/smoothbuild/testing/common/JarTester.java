package org.smoothbuild.testing.common;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.testing.db.values.ValueCreators.blob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.SString;

public class JarTester {

  public static Blob jar(Struct... files) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (JarOutputStream jarOutputStream = new JarOutputStream(outputStream)) {
      for (Struct file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    return blob(memoryValuesDb(), outputStream.toByteArray());
  }

  private static void addEntry(JarOutputStream jarOutputStream, Struct file) throws IOException {
    JarEntry entry = new JarEntry(((SString) file.get("path")).data());
    jarOutputStream.putNextEntry(entry);
    jarOutputStream.write(toByteArray(((Blob) file.get("content")).openInputStream()));
    jarOutputStream.closeEntry();
  }
}
