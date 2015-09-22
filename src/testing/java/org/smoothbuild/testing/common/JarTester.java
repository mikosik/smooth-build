package org.smoothbuild.testing.common;

import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.smoothbuild.testing.db.objects.ValueCreators.blob;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class JarTester {

  public static Blob jar(SFile... files) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (JarOutputStream jarOutputStream = new JarOutputStream(outputStream)) {
      for (SFile file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    return blob(objectsDb(), outputStream.toByteArray());
  }

  private static void addEntry(JarOutputStream jarOutputStream, SFile file) throws IOException {
    JarEntry entry = new JarEntry(file.path().value());
    jarOutputStream.putNextEntry(entry);
    jarOutputStream.write(inputStreamToBytes(file.content().openInputStream()));
    jarOutputStream.closeEntry();
  }
}
