package org.smoothbuild.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.builtin.compress.err.DuplicatePathInZipError;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.builtin.util.EndsWithPredicate;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SValueFactory;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.base.Predicate;

public class Unzipper {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);
  private final byte[] buffer;
  private final SValueFactory valueFactory;
  private DuplicatesDetector<Path> duplicatesDetector;

  public Unzipper(SValueFactory valueFactory) {
    this.valueFactory = valueFactory;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public SArray<SFile> unzip(SBlob zipBlob) {
    this.duplicatesDetector = new DuplicatesDetector<Path>();
    ArrayBuilder<SFile> fileArrayBuilder = valueFactory.arrayBuilder(FILE_ARRAY);
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(zipBlob.openInputStream())) {
        ZipEntry entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          if (!IS_DIRECTORY.apply(entry.getName())) {
            fileArrayBuilder.add(unzipEntry(zipInputStream, entry));
          }
        }
      }
      return fileArrayBuilder.build();
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  private SFile unzipEntry(ZipInputStream zipInputStream, ZipEntry entry) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new IllegalPathInZipError(fileName);
    }
    Path path = path(fileName);
    if (duplicatesDetector.addValue(path)) {
      throw new DuplicatePathInZipError(path);
    }

    SBlob content = unzipEntryContent(zipInputStream);
    return valueFactory.file(path, content);
  }

  private SBlob unzipEntryContent(ZipInputStream zipInputStream) {
    try {
      BlobBuilder contentBuilder = valueFactory.blobBuilder();
      try (OutputStream outputStream = contentBuilder.openOutputStream()) {
        int len = 0;
        while ((len = zipInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return contentBuilder.build();
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }
}
