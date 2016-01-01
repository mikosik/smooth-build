package org.smoothbuild.builtin.compress;

import static java.io.File.createTempFile;
import static org.smoothbuild.io.fs.base.Path.SEPARATOR;
import static org.smoothbuild.io.fs.base.Path.validationError;
import static org.smoothbuild.util.Streams.copy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
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
  public static Array<SFile> unzip(Container container, Blob blob) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    ArrayBuilder<SFile> fileArrayBuilder = container.create().arrayBuilder(SFile.class);
    try {
      File tempFile = copyToTempFile(blob);
      try (ZipFile zipFile = new ZipFile(tempFile)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (!IS_DIR.test(entry.getName())) {
            SFile unzippedEntry = unzipEntry(container, zipFile.getInputStream(entry), entry);
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
    } catch (ZipException e) {
      throw new ErrorMessage("Cannot unzip archive. Corrupted data?");
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private static File copyToTempFile(Blob blob) throws IOException, FileNotFoundException {
    File tempFile = createTempFile("unzip", null);
    copy(blob.openInputStream(), new BufferedOutputStream(new FileOutputStream(tempFile)));
    return tempFile;
  }

  private static SFile unzipEntry(Container container, InputStream inputStream, ZipEntry entry) {
    String fileName = entry.getName();
    String errorMessage = validationError(fileName);
    if (errorMessage != null) {
      throw new ErrorMessage("File in a zip file has illegal name = '" + fileName + "'");
    }

    SString path = container.create().string(fileName);
    Blob content = unzipEntryContent(container, inputStream);
    return container.create().file(path, content);
  }

  private static Blob unzipEntryContent(Container container, InputStream inputStream) {
    byte[] buffer = new byte[Constants.BUFFER_SIZE];
    try {
      BlobBuilder contentBuilder = container.create().blobBuilder();
      try (OutputStream outputStream = contentBuilder) {
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      return contentBuilder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
