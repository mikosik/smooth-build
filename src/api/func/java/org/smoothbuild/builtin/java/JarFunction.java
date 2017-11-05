package org.smoothbuild.builtin.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.DuplicatesDetector;

public class JarFunction {
  @SmoothFunction
  public static Blob jar(Container container, Array files, Blob manifest) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    byte[] buffer = new byte[Constants.BUFFER_SIZE];
    BlobBuilder blobBuilder = container.create().blobBuilder();
    try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder, manifest)) {
      for (Value fileValue : files) {
        SFile file = (SFile) fileValue;
        String path = file.path().value();
        if (duplicatesDetector.addValue(path)) {
          throw new ErrorMessage("Cannot jar two files with the same path = " + path);
        }
        jarFile(file, jarOutputStream, buffer);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return blobBuilder.build();
  }

  private static JarOutputStream createOutputStream(BlobBuilder blobBuilder, Blob manifest)
      throws IOException {
    OutputStream outputStream = blobBuilder;
    if (manifest == null) {
      return new JarOutputStream(outputStream);
    } else {
      try (InputStream manifestStream = manifest.openInputStream()) {
        return new JarOutputStream(outputStream, new Manifest(manifestStream));
      }
    }
  }

  private static void jarFile(SFile file, JarOutputStream jarOutputStream, byte[] buffer)
      throws IOException {
    jarOutputStream.putNextEntry(new JarEntry(file.path().value()));
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
