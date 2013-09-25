package org.smoothbuild.builtin.compress;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.builtin.compress.err.DuplicatePathInZipError;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.task.err.FileSystemError;

public class Unzipper {
  private final byte[] buffer = new byte[Constants.BUFFER_SIZE];

  public void unzipFile(File zipFile, MutableFileSet resultFiles) {
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.openInputStream());) {
        ZipEntry entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          unzipEntry(zipInputStream, entry, resultFiles);
        }
      }
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  private File unzipEntry(ZipInputStream zipInputStream, ZipEntry entry, MutableFileSet resultFiles) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new IllegalPathInZipError(fileName);
    }
    Path path = path(fileName);
    if (resultFiles.contains(path)) {
      throw new DuplicatePathInZipError(path);
    }
    MutableFile file = resultFiles.createFile(path);
    try {
      try (OutputStream outputStream = file.openOutputStream()) {
        int len = 0;
        while ((len = zipInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
    return file;
  }
}
