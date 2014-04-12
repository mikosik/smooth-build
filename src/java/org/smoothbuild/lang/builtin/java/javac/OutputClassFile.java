package org.smoothbuild.lang.builtin.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.util.ForwardingOutputStream;

public class OutputClassFile extends SimpleJavaFileObject {
  private final ArrayBuilder<SFile> fileArrayBuilder;
  private final Path path;
  private final BlobBuilder contentBuilder;
  private final NativeApi nativeApi;

  public OutputClassFile(ArrayBuilder<SFile> fileArrayBuilder, Path path, NativeApi nativeApi) {
    super(URI.create("class:///" + path.value()), Kind.CLASS);
    this.fileArrayBuilder = fileArrayBuilder;
    this.path = path;
    this.nativeApi = nativeApi;
    this.contentBuilder = nativeApi.blobBuilder();
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    final OutputStream outputStream = contentBuilder.openOutputStream();
    return new ForwardingOutputStream(outputStream) {
      @Override
      public void close() throws IOException {
        outputStream.close();

        FileBuilder fileBuilder = nativeApi.fileBuilder();
        fileBuilder.setPath(path);
        fileBuilder.setContent(contentBuilder.build());
        SFile file = fileBuilder.build();

        fileArrayBuilder.add(file);
      }
    };
  }
}
