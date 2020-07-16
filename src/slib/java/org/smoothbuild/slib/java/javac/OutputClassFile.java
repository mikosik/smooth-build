package org.smoothbuild.slib.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.BlobBuilder;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Tuple;
import org.smoothbuild.lang.plugin.NativeApi;

import okio.ForwardingSink;
import okio.Okio;

public class OutputClassFile extends SimpleJavaFileObject {
  private final ArrayBuilder fileArrayBuilder;
  private final Path path;
  private final BlobBuilder contentBuilder;
  private final NativeApi nativeApi;

  public OutputClassFile(ArrayBuilder fileArrayBuilder, Path path, NativeApi nativeApi) {
    super(URI.create("class:///" + path.toString()), Kind.CLASS);
    this.fileArrayBuilder = fileArrayBuilder;
    this.path = path;
    this.nativeApi = nativeApi;
    this.contentBuilder = nativeApi.factory().blobBuilder();
  }

  @Override
  public OutputStream openOutputStream() {
    return Okio.buffer(new ForwardingSink(contentBuilder.sink()) {
      @Override
      public void close() throws IOException {
        super.close();
        SString pathString = nativeApi.factory().string(path.toString());
        Tuple file = nativeApi.factory().file(pathString, contentBuilder.build());
        fileArrayBuilder.add(file);
      }
    }).outputStream();
  }
}
