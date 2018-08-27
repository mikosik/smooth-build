package org.smoothbuild.builtin.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

import okio.ForwardingSink;
import okio.Okio;

public class OutputClassFile extends SimpleJavaFileObject {
  private final ArrayBuilder fileArrayBuilder;
  private final Path path;
  private final BlobBuilder contentBuilder;
  private final NativeApi nativeApi;

  public OutputClassFile(ArrayBuilder fileArrayBuilder, Path path, NativeApi nativeApi) {
    super(URI.create("class:///" + path.value()), Kind.CLASS);
    this.fileArrayBuilder = fileArrayBuilder;
    this.path = path;
    this.nativeApi = nativeApi;
    this.contentBuilder = nativeApi.create().blobBuilder();
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    return Okio.buffer(new ForwardingSink(contentBuilder.sink()) {
      @Override
      public void close() throws IOException {
        super.close();
        SString pathString = nativeApi.create().string(path.value());
        Struct file = nativeApi.create().file(pathString, contentBuilder.build());
        fileArrayBuilder.add(file);
      }
    }).outputStream();
  }
}
