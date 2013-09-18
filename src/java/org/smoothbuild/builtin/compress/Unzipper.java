package org.smoothbuild.builtin.compress;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.builtin.compress.err.DuplicatePathInZipException;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Path;

public class Unzipper {
  private final byte[] buffer = new byte[1024];;

  public void unzipFile(File zipFile, MutableFileSet resultFiles) throws IOException,
      DuplicatePathInZipException, IllegalPathInZipException {
    try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.openInputStream());) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        unzipEntry(zipInputStream, entry, resultFiles);
      }
    }
  }

  private File unzipEntry(ZipInputStream zipInputStream, ZipEntry entry, MutableFileSet resultFiles)
      throws IOException, DuplicatePathInZipException, IllegalPathInZipException {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new IllegalPathInZipException(fileName);
    }
    Path path = path(fileName);
    if (resultFiles.contains(path)) {
      throw new DuplicatePathInZipException(path);
    }
    MutableFile file = resultFiles.createFile(path);
    try (OutputStream outputStream = file.openOutputStream()) {
      int len = 0;
      while ((len = zipInputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, len);
      }
    }
    return file;
  }
}
