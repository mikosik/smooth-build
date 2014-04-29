package org.smoothbuild.testing.common;

import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class JarTester {

  public static SBlob jar(SFile... files) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (JarOutputStream jarOutputStream = new JarOutputStream(outputStream);) {
      for (SFile file : files) {
        addEntry(jarOutputStream, file);
      }
    }

    FakeObjectsDb objectsDb = new FakeObjectsDb();
    return objectsDb.blob(outputStream.toByteArray());
  }

  private static void addEntry(JarOutputStream jarOutputStream, SFile file) throws IOException {
    JarEntry entry = new JarEntry(file.path().value());
    jarOutputStream.putNextEntry(entry);
    jarOutputStream.write(inputStreamToBytes(file.content().openInputStream()));
    jarOutputStream.closeEntry();
  }
}
