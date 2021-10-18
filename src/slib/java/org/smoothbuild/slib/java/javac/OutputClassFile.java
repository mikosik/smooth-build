package org.smoothbuild.slib.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.NativeApi;

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
        Str pathString = nativeApi.factory().string(path.toString());
        Struc_ file = nativeApi.factory().file(pathString, contentBuilder.build());
        fileArrayBuilder.add(file);
      }
    }).outputStream();
  }
}
