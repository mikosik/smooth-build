package org.smoothbuild.slib.java;

import static okio.Okio.buffer;
import static okio.Okio.sink;
import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.bytecode.obj.cnst.BlobBBuilder;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSink;
import okio.BufferedSource;

public class JarFunc {
  public static CnstB func(NativeApi nativeApi, TupleB args) throws IOException {
    ArrayB files = (ArrayB) args.get(0);
    BlobB manifest = (BlobB) args.get(1);

    var duplicatesDetector = new HashSet<String>();
    BlobBBuilder blobBuilder = nativeApi.factory().blobBuilder();
    try (JarOutputStream jarOutputStream = createOutputStream(blobBuilder, manifest)) {
      for (TupleB file : files.elems(TupleB.class)) {
        String path = filePath(file).toJ();
        if (!duplicatesDetector.add(path)) {
          nativeApi.log().error("Cannot jar two files with the same path = " + path);
          return null;
        }
        jarFile(file, jarOutputStream);
      }
    }
    return blobBuilder.build();
  }

  private static JarOutputStream createOutputStream(BlobBBuilder blobBuilder, BlobB manifest)
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

  private static void jarFile(TupleB file, JarOutputStream jarOutputStream) throws IOException {
    jarOutputStream.putNextEntry(new JarEntry(filePath(file).toJ()));
    try (BufferedSource source = fileContent(file).source()) {
      BufferedSink sink = buffer(sink(jarOutputStream));
      source.readAll(sink);
      sink.flush();
    }
    jarOutputStream.closeEntry();
  }
}
