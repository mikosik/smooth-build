package org.smoothbuild.testing.common;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

public class ZipTester {
  public static TestFile zippedFiles(String... fileNames) throws IOException {
    TestFile zipFile = new TestFileSet().createFile(path("file.zip"));
    zipFiles(zipFile, fileNames);
    return zipFile;
  }

  public static void zipFiles(TestFile zipFile, String... fileNames) throws IOException {
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(zipFile.openOutputStream());) {
      for (String fileName : fileNames) {
        addEntry(zipOutputStream, fileName);
      }
    }
  }

  private static void addEntry(ZipOutputStream zipOutputStream, String fileName) throws IOException {
    ZipEntry entry = new ZipEntry(fileName);
    zipOutputStream.putNextEntry(entry);

    OutputStreamWriter writer = new OutputStreamWriter(zipOutputStream);
    writer.write(fileName);
    writer.flush();

    zipOutputStream.closeEntry();
  }
}
