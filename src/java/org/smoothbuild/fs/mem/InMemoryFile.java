package org.smoothbuild.fs.mem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.smoothbuild.fs.base.FileSystemException;

public class InMemoryFile implements InMemoryElement {
  private final String name;
  private byte data[];

  public InMemoryFile(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean isFile() {
    return true;
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  @Override
  public boolean hasChild(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public InMemoryElement child(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<String> childNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addChild(InMemoryElement newDir) {
    throw new UnsupportedOperationException();
  }

  @Override
  public InputStream createInputStream() {
    if (data == null) {
      throw new FileSystemException("File does not exist");
    }
    return new ByteArrayInputStream(data);
  }

  @Override
  public OutputStream createOutputStream() {
    return new InMemoryOutputStream();
  }

  private class InMemoryOutputStream extends ByteArrayOutputStream {
    @Override
    public void close() {
      InMemoryFile.this.data = toByteArray();
    }
  }
}
