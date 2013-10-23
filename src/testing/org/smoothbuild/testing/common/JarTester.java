package org.smoothbuild.testing.common;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.type.impl.FakeFile;

public class JarTester {
  public static FakeFile jaredFiles(String... fileNames) throws IOException {
    return jaredFiles(new FakeFileSystem(), fileNames);
  }

  public static FakeFile jaredFiles(FakeFileSystem fileSystem, String... fileNames)
      throws IOException {
    Path path = Path.path("input.jar");
    try (JarOutputStream jarOutputStream = new JarOutputStream(fileSystem.openOutputStream(path));) {
      for (String fileName : fileNames) {
        addEntry(jarOutputStream, fileName);
      }
    }

    return new FakeFile(fileSystem, path);
  }

  private static void addEntry(JarOutputStream jarOutputStream, String fileName) throws IOException {
    JarEntry entry = new JarEntry(fileName);
    jarOutputStream.putNextEntry(entry);

    OutputStreamWriter writer = new OutputStreamWriter(jarOutputStream);
    writer.write(fileName);
    writer.flush();

    jarOutputStream.closeEntry();
  }
}
