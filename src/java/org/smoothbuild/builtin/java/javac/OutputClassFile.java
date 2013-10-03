package org.smoothbuild.builtin.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import org.smoothbuild.type.api.MutableFile;

public class OutputClassFile extends SimpleJavaFileObject {
  private final MutableFile file;

  public OutputClassFile(MutableFile file) {
    super(URI.create("class:///" + file.path().value()), Kind.CLASS);
    this.file = file;
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    return file.openOutputStream();
  }
}
