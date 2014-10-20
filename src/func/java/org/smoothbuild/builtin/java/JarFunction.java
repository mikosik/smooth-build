package org.smoothbuild.builtin.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.builtin.java.err.CannotAddDuplicatePathError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.util.DuplicatesDetector;

public class JarFunction {
  public interface JarParameters {
    @Required
    public SArray<SFile> files();

    public SBlob manifest();
  }

  @SmoothFunction(name = "jar")
  public static SBlob jar(NativeApi nativeApi, JarParameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApi nativeApi;
    private final JarParameters params;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];
    private final DuplicatesDetector<Path> duplicatesDetector;

    public Worker(NativeApi nativeApi, JarParameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
      this.duplicatesDetector = new DuplicatesDetector<>();
    }

    public SBlob execute() {
      BlobBuilder blobBuilder = nativeApi.blobBuilder();
      try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder)) {
        for (SFile file : params.files()) {
          addEntry(jarOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemError(e);
      }

      return blobBuilder.build();
    }

    private JarOutputStream createOutputStream(BlobBuilder blobBuilder) throws IOException {
      OutputStream outputStream = blobBuilder.openOutputStream();
      if (params.manifest() == null) {
        return new JarOutputStream(outputStream);
      } else {
        try (InputStream manifestStream = params.manifest().openInputStream()) {
          Manifest manifest = new Manifest(manifestStream);
          return new JarOutputStream(outputStream, manifest);
        }
      }
    }

    private void addEntry(JarOutputStream jarOutputStream, SFile file) throws IOException {
      Path path = file.path();
      if (duplicatesDetector.addValue(path)) {
        throw new CannotAddDuplicatePathError(path);
      }
      JarEntry entry = new JarEntry(path.value());
      jarOutputStream.putNextEntry(entry);

      try (InputStream inputStream = file.content().openInputStream()) {
        int readCount = inputStream.read(buffer);
        while (readCount > 0) {
          jarOutputStream.write(buffer, 0, readCount);
          readCount = inputStream.read(buffer);
        }
      }

      jarOutputStream.closeEntry();
    }
  }
}
