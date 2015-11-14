package org.smoothbuild.io.fs.mem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemException;

public class MemoryFile implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private byte data[];

  public MemoryFile(MemoryDir parent, Path name) {
    this.parent = parent;
    this.name = name;
  }

  @Override
  public Path name() {
    return name;
  }

  @Override
  public MemoryDir parent() {
    return parent;
  }

  @Override
  public boolean isFile() {
    return true;
  }

  @Override
  public boolean isDir() {
    return false;
  }

  @Override
  public boolean hasChild(Path name) {
    return false;
  }

  @Override
  public MemoryElement child(Path name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Path> childNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addChild(MemoryElement element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public InputStream openInputStream() {
    if (data == null) {
      throw new FileSystemException("File does not exist");
    }
    return new ByteArrayInputStream(data);
  }

  @Override
  public OutputStream openOutputStream() {
    return new MemoryOutputStream();
  }

  private class MemoryOutputStream extends ByteArrayOutputStream {
    @Override
    public void close() {
      MemoryFile.this.data = toByteArray();
    }
  }
}
