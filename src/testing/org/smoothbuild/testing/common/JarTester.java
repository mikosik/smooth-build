package org.smoothbuild.testing.common;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.testing.type.impl.TestFileSet;

public class JarTester {
  public static TestFile jaredFiles(String... fileNames) throws IOException {
    TestFile jarFile = new TestFileSet().createFile(path("input.jar"));
    jarFiles(jarFile, fileNames);
    return jarFile;
  }

  public static void jarFiles(TestFile jarFile, String... fileNames) throws IOException {
    try (JarOutputStream jarOutputStream = new JarOutputStream(jarFile.openOutputStream());) {
      for (String fileName : fileNames) {
        addEntry(jarOutputStream, fileName);
      }
    }
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
