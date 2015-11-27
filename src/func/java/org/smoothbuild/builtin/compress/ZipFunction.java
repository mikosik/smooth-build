package org.smoothbuild.builtin.compress;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.DuplicatesDetector;

public class ZipFunction {

  // add missing parameters: level, comment, method

  @SmoothFunction
  public static Blob zip(
      Container container,
      @Required @Name("file") Array<SFile> files) {
    return new Worker(container, files).execute();
  }

  private static class Worker {
    private final Container container;
    private final Array<SFile> files;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];
    private final DuplicatesDetector<String> duplicatesDetector;

    public Worker(Container container, Array<SFile> files) {
      this.container = container;
      this.files = files;
      this.duplicatesDetector = new DuplicatesDetector<>();
    }

    public Blob execute() {
      BlobBuilder blobBuilder = container.create().blobBuilder();

      try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.openOutputStream())) {
        for (SFile file : files) {
          addEntry(zipOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return blobBuilder.build();
    }

    private void addEntry(ZipOutputStream zipOutputStream, SFile file) throws IOException {
      String path = file.path().value();
      if (duplicatesDetector.addValue(path)) {
        throw new Message(ERROR, "Cannot zip two files with the same path = " + path);
      }
      ZipEntry entry = new ZipEntry(path);
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
}
