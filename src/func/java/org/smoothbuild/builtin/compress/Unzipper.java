package org.smoothbuild.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.validationError;
import static org.smoothbuild.lang.message.MessageType.ERROR;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.DuplicatesDetector;

public class Unzipper {
  private static final Predicate<String> IS_DIR = (string) -> string.endsWith(SEPARATOR);
  private final byte[] buffer;
  private final Container container;
  private DuplicatesDetector<String> duplicatesDetector;

  public Unzipper(Container container) {
    this.container = container;
    this.buffer = new byte[Constants.BUFFER_SIZE];
  }

  public Array<SFile> unzip(Blob zipBlob) {
    this.duplicatesDetector = new DuplicatesDetector<>();
    ArrayBuilder<SFile> fileArrayBuilder = container.create().arrayBuilder(SFile.class);
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(zipBlob.openInputStream())) {
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          if (!IS_DIR.test(entry.getName())) {
            fileArrayBuilder.add(unzipEntry(zipInputStream, entry));
          }
        }
      }
      return fileArrayBuilder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private SFile unzipEntry(ZipInputStream zipInputStream, ZipEntry entry) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new IllegalPathInZipError(fileName);
    }
    if (duplicatesDetector.addValue(fileName)) {
      throw new Message(ERROR, "Zip file contains two files with the same path = " + fileName);
    }

    SString path = container.create().string(fileName);
    Blob content = unzipEntryContent(zipInputStream);
    return container.create().file(path, content);
  }

  private Blob unzipEntryContent(ZipInputStream zipInputStream) {
    try {
      BlobBuilder contentBuilder = container.create().blobBuilder();
      try (OutputStream outputStream = contentBuilder.openOutputStream()) {
        int len;
        while ((len = zipInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return contentBuilder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
