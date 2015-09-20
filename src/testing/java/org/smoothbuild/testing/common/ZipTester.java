package org.smoothbuild.testing.common;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;

public class ZipTester {

  public static Path zippedFiles(FileSystem fileSystem, String... fileNames) throws IOException {
    Path path = path("input.zip");
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileSystem.openOutputStream(path))) {
      for (String fileName : fileNames) {
        addEntry(zipOutputStream, fileName);
      }
    }

    return path;
  }

  private static void addEntry(ZipOutputStream zipOutputStream, String fileName)
      throws IOException {
    ZipEntry entry = new ZipEntry(fileName);
    zipOutputStream.putNextEntry(entry);

    OutputStreamWriter writer = new OutputStreamWriter(zipOutputStream);
    writer.write(fileName);
    writer.flush();

    zipOutputStream.closeEntry();
  }
}
