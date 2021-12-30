package org.smoothbuild.slib.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.bytecode.obj.val.ArrayBBuilder;
import org.smoothbuild.bytecode.obj.val.BlobBBuilder;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.NativeApi;

import okio.ForwardingSink;
import okio.Okio;

public class OutputClassFile extends SimpleJavaFileObject {
  private final ArrayBBuilder fileArrayBuilder;
  private final Path path;
  private final BlobBBuilder contentBuilder;
  private final NativeApi nativeApi;

  public OutputClassFile(ArrayBBuilder fileArrayBuilder, Path path, NativeApi nativeApi) {
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
        StringB pathString = nativeApi.factory().string(path.toString());
        TupleB file = nativeApi.factory().file(pathString, contentBuilder.build());
        fileArrayBuilder.add(file);
      }
    }).outputStream();
  }
}
