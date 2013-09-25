package org.smoothbuild.fs.mem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.smoothbuild.task.err.FileSystemError;

public class MemoryFile implements MemoryElement {
  private final MemoryDirectory parent;
  private final String name;
  private byte data[];

  public MemoryFile(MemoryDirectory parent, String name) {
    this.parent = parent;
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public MemoryDirectory parent() {
    return parent;
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
    return false;
  }

  @Override
  public MemoryElement child(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<String> childNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addChild(MemoryElement newDir) {
    throw new UnsupportedOperationException();
  }

  @Override
  public InputStream createInputStream() {
    if (data == null) {
      throw new FileSystemError("File does not exist");
    }
    return new ByteArrayInputStream(data);
  }

  @Override
  public OutputStream createOutputStream() {
    return new MemoryOutputStream();
  }

  private class MemoryOutputStream extends ByteArrayOutputStream {
    @Override
    public void close() {
      MemoryFile.this.data = toByteArray();
    }
  }
}
