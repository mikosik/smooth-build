package org.smoothbuild.lang.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.validationError;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.builtin.compress.err.DuplicatePathInZipError;
import org.smoothbuild.lang.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.util.DuplicatesDetector;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;

public class Unzipper {
  private static final Predicate<String> IS_DIRECTORY = new EndsWithPredicate(SEPARATOR);
  private final byte[] buffer;
  private final NativeApi nativeApi;
  private DuplicatesDetector<Path> duplicatesDetector;

  public Unzipper(NativeApi nativeApi) {
    this.nativeApi = nativeApi;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public SArray<SFile> unzip(SBlob zipBlob) {
    this.duplicatesDetector = new DuplicatesDetector<Path>();
    ArrayBuilder<SFile> fileArrayBuilder = nativeApi.arrayBuilder(FILE_ARRAY);
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(zipBlob.openInputStream());) {
        ZipEntry entry = null;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          if (!IS_DIRECTORY.apply(entry.getName())) {
            fileArrayBuilder.add(unzipEntry(zipInputStream, entry, fileArrayBuilder));
          }
        }
      }
      return fileArrayBuilder.build();
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  private SFile unzipEntry(ZipInputStream zipInputStream, ZipEntry entry,
      ArrayBuilder<SFile> fileArrayBuilder) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new IllegalPathInZipError(fileName);
    }
    Path path = path(fileName);
    if (duplicatesDetector.addValue(path)) {
      throw new DuplicatePathInZipError(path);
    }

    FileBuilder fileBuilder = nativeApi.fileBuilder();
    fileBuilder.setPath(path);
    fileBuilder.setContent(unzipEntryContent(zipInputStream));
    return fileBuilder.build();
  }

  private SBlob unzipEntryContent(ZipInputStream zipInputStream) {
    try {
      BlobBuilder contentBuilder = nativeApi.blobBuilder();
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
