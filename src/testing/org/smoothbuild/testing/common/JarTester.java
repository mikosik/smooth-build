package org.smoothbuild.testing.common;

import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.plugin.FakeFile;

public class JarTester {
  public static FakeFile jaredFiles(String... fileNames) throws IOException {
    return jaredFiles(new FakeFileSystem(), fileNames);
  }

  public static FakeFile jaredFiles(FakeFileSystem fileSystem, String... fileNames)
      throws IOException {
    Path path = Path.path("input.jar");
    OutputStream outputStream = fileSystem.openOutputStream(path);
    try (JarOutputStream jarOutputStream = new JarOutputStream(outputStream);) {
      for (String fileName : fileNames) {
        addEntry(jarOutputStream, fileName);
      }
    }

    return new FakeFile(path, inputStreamToBytes(fileSystem.openInputStream(path)));
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
