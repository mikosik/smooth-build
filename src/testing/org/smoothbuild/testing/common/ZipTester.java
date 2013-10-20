package org.smoothbuild.testing.common;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.testing.type.impl.FakeFile;
import org.smoothbuild.testing.type.impl.FakeFileSet;

public class ZipTester {
  public static FakeFile zippedFiles(String... fileNames) throws IOException {
    FakeFile zipFile = new FakeFileSet().createFile(path("file.zip"));
    zipFiles(zipFile, fileNames);
    return zipFile;
  }

  public static void zipFiles(FakeFile zipFile, String... fileNames) throws IOException {
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
