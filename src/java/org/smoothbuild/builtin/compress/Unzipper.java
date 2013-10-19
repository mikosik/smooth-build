package org.smoothbuild.builtin.compress;

import static org.smoothbuild.fs.base.Path.SEPARATOR;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.base.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.builtin.compress.err.DuplicatePathInZipError;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.type.api.File;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;

public class Unzipper {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);
  private final byte[] buffer = new byte[Constants.BUFFER_SIZE];

  public void unzipFile(File zipFile, FileSetBuilder resultFiles) {
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.openInputStream());) {
        ZipEntry entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          if (!IS_DIRECTORY.apply(entry.getName())) {
            unzipEntry(zipInputStream, entry, resultFiles);
          }
        }
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private void unzipEntry(ZipInputStream zipInputStream, ZipEntry entry, FileSetBuilder resultFiles) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new ErrorMessageException(new IllegalPathInZipError(fileName));
    }
    Path path = path(fileName);
    if (resultFiles.contains(path)) {
      throw new ErrorMessageException(new DuplicatePathInZipError(path));
    }
    try {
      try (OutputStream outputStream = resultFiles.openFileOutputStream(path)) {
        int len = 0;
        while ((len = zipInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
