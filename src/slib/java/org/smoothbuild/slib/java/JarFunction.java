package org.smoothbuild.slib.java;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.DuplicatesDetector;

import okio.BufferedSink;
import okio.BufferedSource;

public class JarFunction {
  public static BlobH function(NativeApi nativeApi, ArrayH files, BlobH manifest) throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    BlobHBuilder blobBuilder = nativeApi.factory().blobBuilder();
    try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder, manifest)) {
      for (TupleH file : files.elems(TupleH.class)) {
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

  private static JarOutputStream createOutputStream(BlobHBuilder blobBuilder, BlobH manifest)
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

  private static void jarFile(TupleH file, JarOutputStream jarOutputStream) throws IOException {
    jarOutputStream.putNextEntry(new JarEntry(filePath(file).jValue()));
    try (BufferedSource source = fileContent(file).source()) {
      BufferedSink sink = buffer(sink(jarOutputStream));
      source.readAll(sink);
      sink.flush();
    }
    jarOutputStream.closeEntry();
  }
}
