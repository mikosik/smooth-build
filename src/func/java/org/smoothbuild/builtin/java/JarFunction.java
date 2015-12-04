package org.smoothbuild.builtin.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.io.fs.base.err.FileSystemException;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.DuplicatesDetector;

public class JarFunction {
  @SmoothFunction
  public static Blob jar(
      Container container,
      @Name("files") Array<SFile> files,
      @Name("manifest") Blob manifest) {
    return new Worker(container, files, manifest).execute();
  }

  private static class Worker {
    private final Container container;
    private final Array<SFile> files;
    private final Blob manifest;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];
    private final DuplicatesDetector<String> duplicatesDetector;

    public Worker(Container container, Array<SFile> files, Blob manifest) {
      this.container = container;
      this.files = files;
      this.manifest = manifest;
      this.duplicatesDetector = new DuplicatesDetector<>();
    }

    public Blob execute() {
      BlobBuilder blobBuilder = container.create().blobBuilder();
      try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder)) {
        for (SFile file : files) {
          addEntry(jarOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return blobBuilder.build();
    }

    private JarOutputStream createOutputStream(BlobBuilder blobBuilder) throws IOException {
      OutputStream outputStream = blobBuilder.openOutputStream();
      if (manifest == null) {
        return new JarOutputStream(outputStream);
      } else {
        try (InputStream manifestStream = manifest.openInputStream()) {
          Manifest manifest = new Manifest(manifestStream);
          return new JarOutputStream(outputStream, manifest);
        }
      }
    }

    private void addEntry(JarOutputStream jarOutputStream, SFile file) throws IOException {
      String path = file.path().value();
      if (duplicatesDetector.addValue(path)) {
        throw new ErrorMessage("Cannot jar two files with the same path = " + path);
      }
      JarEntry entry = new JarEntry(path);
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
