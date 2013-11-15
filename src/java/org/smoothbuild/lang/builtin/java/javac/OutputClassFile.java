package org.smoothbuild.lang.builtin.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.FileSetBuilder;
import org.smoothbuild.util.ForwardingOutputStream;

public class OutputClassFile extends SimpleJavaFileObject {
  private final FileSetBuilder fileSetBuilder;
  private final FileBuilder fileBuilder;

  public OutputClassFile(FileSetBuilder fileSetBuilder, Path path, FileBuilder fileBuilder) {
    super(URI.create("class:///" + path.value()), Kind.CLASS);
    this.fileSetBuilder = fileSetBuilder;
    this.fileBuilder = fileBuilder;
    fileBuilder.setPath(path);
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    final OutputStream outputStream = fileBuilder.openOutputStream();
    return new ForwardingOutputStream(outputStream) {
      @Override
      public void close() throws IOException {
        outputStream.close();
        fileSetBuilder.add(fileBuilder.build());
      }
    };
  }
}
