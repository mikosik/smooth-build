package org.smoothbuild.builtin.compress;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.builtin.compress.err.CannotAddDuplicatePathError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
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
  public static Blob zip( //
      NativeApi nativeApi, //
      @Required @Name("file") Array<SFile> files) {
    return new Worker(nativeApi, files).execute();
  }

  private static class Worker {
    private final NativeApi nativeApi;
    private final Array<SFile> files;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];
    private final DuplicatesDetector<Path> duplicatesDetector;

    public Worker(NativeApi nativeApi, Array<SFile> files) {
      this.nativeApi = nativeApi;
      this.files = files;
      this.duplicatesDetector = new DuplicatesDetector<>();
    }

    public Blob execute() {
      BlobBuilder blobBuilder = nativeApi.blobBuilder();

      try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.openOutputStream())) {
        for (SFile file : files) {
          addEntry(zipOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemError(e);
      }

      return blobBuilder.build();
    }

    private void addEntry(ZipOutputStream zipOutputStream, SFile file) throws IOException {
      Path path = file.path();
      if (duplicatesDetector.addValue(path)) {
        throw new CannotAddDuplicatePathError(path);
      }
      ZipEntry entry = new ZipEntry(path.value());
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
