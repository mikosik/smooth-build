package org.smoothbuild.lang.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.builtin.compress.err.DuplicatePathInZipError;
import org.smoothbuild.lang.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class Unzipper {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);
  private final byte[] buffer;
  private final Sandbox sandbox;
  private Set<Path> alreadyUnzipped;

  public Unzipper(Sandbox sandbox) {
    this.sandbox = sandbox;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public SArray<SFile> unzipFile(SFile zipFile) {
    this.alreadyUnzipped = Sets.newHashSet();
    ArrayBuilder<SFile> fileArrayBuilder = sandbox.arrayBuilder(FILE_ARRAY);
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.openInputStream());) {
        ZipEntry entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          if (!IS_DIRECTORY.apply(entry.getName())) {
            fileArrayBuilder.add(unzipEntry(zipInputStream, entry, fileArrayBuilder));
          }
        }
      }
      return fileArrayBuilder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private SFile unzipEntry(ZipInputStream zipInputStream, ZipEntry entry,
      ArrayBuilder<SFile> fileArrayBuilder) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new ErrorMessageException(new IllegalPathInZipError(fileName));
    }
    Path path = path(fileName);
    if (alreadyUnzipped.contains(path)) {
      throw new ErrorMessageException(new DuplicatePathInZipError(path));
    }
    alreadyUnzipped.add(path);
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
