package org.smoothbuild.lang.builtin.compress;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.builtin.compress.err.CannotAddDuplicatePathError;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.util.DuplicatesDetector;

public class ZipFunction {

  public interface Parameters {
    @Required
    public SArray<SFile> files();

    // add missing parameters: level, comment, method
  }

  @SmoothFunction(name = "zip")
  public static SBlob execute(NativeApi nativeApi, Parameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApi nativeApi;
    private final Parameters params;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];
    private final DuplicatesDetector<Path> duplicatesDetector;

    public Worker(NativeApi nativeApi, Parameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
      this.duplicatesDetector = new DuplicatesDetector<Path>();
    }

    public SBlob execute() {
      BlobBuilder blobBuilder = nativeApi.blobBuilder();

      try (ZipOutputStream zipOutputStream = new ZipOutputStream(blobBuilder.openOutputStream());) {
        for (SFile file : params.files()) {
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

      try (InputStream inputStream = file.content().openInputStream();) {
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
