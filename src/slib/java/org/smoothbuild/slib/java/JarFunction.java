package org.smoothbuild.slib.java;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.smoothbuild.db.record.db.FileStruct.fileContent;
import static org.smoothbuild.db.record.db.FileStruct.filePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.BlobBuilder;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.util.DuplicatesDetector;

import okio.BufferedSink;
import okio.BufferedSource;

public class JarFunction {
  @SmoothFunction("jar")
  public static Blob jar(NativeApi nativeApi, Array files, Blob manifest) throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    BlobBuilder blobBuilder = nativeApi.factory().blobBuilder();
    try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder, manifest)) {
      for (Tuple file : files.asIterable(Tuple.class)) {
        String path = filePath(file).jValue();
        if (duplicatesDetector.addValue(path)) {
          nativeApi.log().error("Cannot jar two files with the same path = " + path);
          return null;
        }
        jarFile(file, jarOutputStream);
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

  private static void jarFile(Tuple file, JarOutputStream jarOutputStream)
      throws IOException {
    jarOutputStream.putNextEntry(new JarEntry(filePath(file).jValue()));
    try (BufferedSource source = fileContent(file).source()) {
      BufferedSink sink = buffer(sink(jarOutputStream));
      source.readAll(sink);
      sink.flush();
    }
    jarOutputStream.closeEntry();
  }
}
