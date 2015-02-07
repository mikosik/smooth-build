package org.smoothbuild.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.builtin.compress.err.DuplicatePathInZipError;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.builtin.util.EndsWithPredicate;
import org.smoothbuild.builtin.util.Predicate;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.DuplicatesDetector;

public class Unzipper {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);
  private final byte[] buffer;
  private final NativeApi nativeApi;
  private DuplicatesDetector<Path> duplicatesDetector;

  public Unzipper(NativeApi nativeApi) {
    this.nativeApi = nativeApi;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public Array<SFile> unzip(Blob zipBlob) {
    this.duplicatesDetector = new DuplicatesDetector<>();
    ArrayBuilder<SFile> fileArrayBuilder = nativeApi.arrayBuilder(SFile.class);
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(zipBlob.openInputStream())) {
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          if (!IS_DIRECTORY.test(entry.getName())) {
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

    Blob content = unzipEntryContent(zipInputStream);
    return nativeApi.file(path, content);
  }

  private Blob unzipEntryContent(ZipInputStream zipInputStream) {
    try {
      BlobBuilder contentBuilder = nativeApi.blobBuilder();
      try (OutputStream outputStream = contentBuilder.openOutputStream()) {
        int len;
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
