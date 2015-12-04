package org.smoothbuild.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.validationError;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.DuplicatesDetector;

public class UnzipFunction {
  private static final Predicate<String> IS_DIR = (string) -> string.endsWith(SEPARATOR);

  @SmoothFunction
  public static Array<SFile> unzip(
      Container container,
      @Name("blob") Blob blob) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    ArrayBuilder<SFile> fileArrayBuilder = container.create().arrayBuilder(SFile.class);
    try {
      try (ZipInputStream zipInputStream = new ZipInputStream(blob.openInputStream())) {
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
          if (!IS_DIR.test(entry.getName())) {
            SFile unzippedEntry = unzipEntry(container, zipInputStream, entry);
            String fileName = unzippedEntry.path().value();
            if (duplicatesDetector.addValue(fileName)) {
              throw new ErrorMessage("Zip file contains two files with the same path = "
                  + fileName);
            }

            fileArrayBuilder.add(unzippedEntry);
          }
        }
      }
      return fileArrayBuilder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private static SFile unzipEntry(Container container, ZipInputStream zipInputStream,
      ZipEntry entry) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new ErrorMessage("File in a zip file has illegal name = '" + fileName + "'");
    }

    SString path = container.create().string(fileName);
    Blob content = unzipEntryContent(container, zipInputStream);
    return container.create().file(path, content);
  }

  private static Blob unzipEntryContent(Container container, ZipInputStream zipInputStream) {
    byte[] buffer = new byte[Constants.BUFFER_SIZE];
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
