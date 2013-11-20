package org.smoothbuild.lang.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.builtin.compress.err.DuplicatePathInZipError;
import org.smoothbuild.lang.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.FileSetBuilder;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;

public class Unzipper {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);
  private final byte[] buffer;
  private final Sandbox sandbox;

  public Unzipper(Sandbox sandbox) {
    this.sandbox = sandbox;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public Array<File> unzipFile(File zipFile) {
    FileSetBuilder fileSetBuilder = sandbox.fileSetBuilder();
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.openInputStream());) {
        ZipEntry entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          if (!IS_DIRECTORY.apply(entry.getName())) {
            fileSetBuilder.add(unzipEntry(zipInputStream, entry, fileSetBuilder));
          }
        }
      }
      return fileSetBuilder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private File unzipEntry(ZipInputStream zipInputStream, ZipEntry entry,
      FileSetBuilder fileSetBuilder) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new ErrorMessageException(new IllegalPathInZipError(fileName));
    }
    Path path = path(fileName);
    if (fileSetBuilder.contains(path)) {
      throw new ErrorMessageException(new DuplicatePathInZipError(path));
    }
    try {
      FileBuilder fileBuilder = sandbox.fileBuilder();
      fileBuilder.setPath(path);

      try (OutputStream outputStream = fileBuilder.openOutputStream()) {
        int len = 0;
        while ((len = zipInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return fileBuilder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
