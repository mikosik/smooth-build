package org.smoothbuild.builtin.compress;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.DuplicatesDetector;

public class ZipFunction {
  @SmoothFunction
  public static Blob zip(Container container, Array<SFile> files) {
    byte[] buffer = new byte[Constants.BUFFER_SIZE];
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    BlobBuilder blobBuilder = container.create().blobBuilder();
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.openOutputStream())) {
      for (SFile file : files) {
        String path = file.path().value();
        if (duplicatesDetector.addValue(path)) {
          throw new ErrorMessage("Cannot zip two files with the same path = " + path);
        }
        zipFile(file, zipOutputStream, buffer);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return blobBuilder.build();
  }

  private static void zipFile(SFile file, ZipOutputStream zipOutputStream, byte[] buffer)
      throws IOException {
    ZipEntry entry = new ZipEntry(file.path().value());
    zipOutputStream.putNextEntry(entry);
    try (InputStream inputStream = file.content().openInputStream()) {
      int readCount = inputStream.read(buffer);
      while (readCount > 0) {
        zipOutputStream.write(buffer, 0, readCount);
        readCount = inputStream.read(buffer);
      }
    }
    zipOutputStream.closeEntry();
  }
}
