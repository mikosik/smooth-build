package org.smoothbuild.testing.common;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.type.impl.FakeFile;

public class ZipTester {
  public static FakeFile zippedFiles(String... fileNames) throws IOException {
    FakeFileSystem fileSystem = new FakeFileSystem();
    return zippedFiles(fileSystem, fileNames);
  }

  public static FakeFile zippedFiles(FakeFileSystem fileSystem, String... fileNames)
      throws IOException {
    Path path = path("input.zip");
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileSystem.openOutputStream(path));) {
      for (String fileName : fileNames) {
        addEntry(zipOutputStream, fileName);
      }
    }

    return new FakeFile(fileSystem, path);
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
