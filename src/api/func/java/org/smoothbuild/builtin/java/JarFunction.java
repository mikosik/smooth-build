package org.smoothbuild.builtin.java;

import static org.smoothbuild.lang.message.MessageException.errorException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.DuplicatesDetector;

public class JarFunction {
  @SmoothFunction
  public static Blob jar(NativeApi nativeApi, Array files, Blob manifest) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    byte[] buffer = new byte[Constants.BUFFER_SIZE];
    BlobBuilder blobBuilder = nativeApi.create().blobBuilder();
    try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder, manifest)) {
      for (Struct file : files.asIterable(Struct.class)) {
        String path = ((SString) file.get("path")).value();
        if (duplicatesDetector.addValue(path)) {
          throw errorException("Cannot jar two files with the same path = " + path);
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

  private static void jarFile(Struct file, JarOutputStream jarOutputStream, byte[] buffer)
      throws IOException {
    jarOutputStream.putNextEntry(new JarEntry(((SString) file.get("path")).value()));
    try (InputStream inputStream = ((Blob) file.get("content")).openInputStream()) {
      int readCount = inputStream.read(buffer);
      while (readCount > 0) {
        jarOutputStream.write(buffer, 0, readCount);
        readCount = inputStream.read(buffer);
      }
    }
    jarOutputStream.closeEntry();
  }
}
