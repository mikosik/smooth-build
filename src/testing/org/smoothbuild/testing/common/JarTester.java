package org.smoothbuild.testing.common;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

public class JarTester {
  public static TestFile jaredFiles(String... fileNames) throws IOException {
    TestFile inputFile = new TestFileSet().createFile(path("input.jar"));

    try (JarOutputStream jarOutputStream = new JarOutputStream(inputFile.openOutputStream());) {
      for (String fileName : fileNames) {
        addEntry(jarOutputStream, fileName);
      }
    }
    return inputFile;
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
