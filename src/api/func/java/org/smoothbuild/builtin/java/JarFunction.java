package org.smoothbuild.builtin.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.builtin.compress.Constants;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.util.DuplicatesDetector;

public class JarFunction {
  @SmoothFunction
  public static Blob jar(NativeApi nativeApi, Array files, Blob manifest, Array javaHash)
      throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    byte[] buffer = new byte[Constants.BUFFER_SIZE];
    BlobBuilder blobBuilder = nativeApi.create().blobBuilder();
    try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder, manifest)) {
      for (Struct file : files.asIterable(Struct.class)) {
        String path = ((SString) file.get("path")).data();
        if (duplicatesDetector.addValue(path)) {
          nativeApi.log().error("Cannot jar two files with the same path = " + path);
          return null;
        }
        jarFile(file, jarOutputStream, buffer);
      }
    }
    return blobBuilder.build();
  }

  private static JarOutputStream createOutputStream(BlobBuilder blobBuilder, Blob manifest)
      throws IOException {
    OutputStream outputStream = blobBuilder.sink().outputStream();
    if (manifest == null) {
      return new JarOutputStream(outputStream);
    } else {
      try (InputStream manifestStream = manifest.source().inputStream()) {
        return new JarOutputStream(outputStream, new Manifest(manifestStream));
      }
    }
  }

  private static void jarFile(Struct file, JarOutputStream jarOutputStream, byte[] buffer)
      throws IOException {
    jarOutputStream.putNextEntry(new JarEntry(((SString) file.get("path")).data()));
    try (InputStream inputStream = ((Blob) file.get("content")).source().inputStream()) {
      int readCount = inputStream.read(buffer);
      while (readCount > 0) {
        jarOutputStream.write(buffer, 0, readCount);
        readCount = inputStream.read(buffer);
      }
    }
    jarOutputStream.closeEntry();
  }
}
