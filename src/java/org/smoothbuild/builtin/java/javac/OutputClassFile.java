package org.smoothbuild.builtin.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.fs.base.Path;

public class OutputClassFile extends SimpleJavaFileObject {
  private final OutputStream outputStream;

  public OutputClassFile(Path path, OutputStream outputStream) {
    super(URI.create("class:///" + path.value()), Kind.CLASS);
    this.outputStream = outputStream;
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    return outputStream;
  }
}
