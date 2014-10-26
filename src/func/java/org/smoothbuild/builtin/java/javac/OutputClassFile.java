package org.smoothbuild.builtin.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.ValueFactory;
import org.smoothbuild.util.ForwardingOutputStream;

public class OutputClassFile extends SimpleJavaFileObject {
  private final ArrayBuilder<SFile> fileArrayBuilder;
  private final Path path;
  private final BlobBuilder contentBuilder;
  private final ValueFactory valueFactory;

  public OutputClassFile(ArrayBuilder<SFile> fileArrayBuilder, Path path, ValueFactory valueFactory) {
    super(URI.create("class:///" + path.value()), Kind.CLASS);
    this.fileArrayBuilder = fileArrayBuilder;
    this.path = path;
    this.valueFactory = valueFactory;
    this.contentBuilder = valueFactory.blobBuilder();
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    final OutputStream outputStream = contentBuilder.openOutputStream();
    return new ForwardingOutputStream(outputStream) {
      @Override
      public void close() throws IOException {
        outputStream.close();
        SFile file = valueFactory.file(path, contentBuilder.build());
        fileArrayBuilder.add(file);
      }
    };
  }
}
