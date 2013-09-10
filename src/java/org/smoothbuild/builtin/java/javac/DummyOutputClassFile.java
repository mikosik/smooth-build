package org.smoothbuild.builtin.java.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import com.google.common.io.ByteStreams;

public class DummyOutputClassFile extends SimpleJavaFileObject {
  public DummyOutputClassFile(String filePath) {
    super(URI.create("class://" + filePath), Kind.CLASS);
  }

  @Override
  public OutputStream openOutputStream() throws IOException {
    return ByteStreams.nullOutputStream();
  }
}
