package org.smoothbuild.io.fs.mem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;

public class MemoryFile implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private byte data[];

  public MemoryFile(MemoryDir parent, Path name) {
    this.parent = parent;
    this.name = name;
  }

  public Path name() {
    return name;
  }

  public MemoryDir parent() {
    return parent;
  }

  public boolean isFile() {
    return true;
  }

  public boolean isDir() {
    return false;
  }

  public boolean hasChild(Path name) {
    return false;
  }

  public MemoryElement child(Path name) {
    throw new UnsupportedOperationException();
  }

  public List<Path> childNames() {
    throw new UnsupportedOperationException();
  }

  public void addChild(MemoryElement element) {
    throw new UnsupportedOperationException();
  }

  public InputStream openInputStream() {
    if (data == null) {
      throw new FileSystemException("File does not exist");
    }
    return new ByteArrayInputStream(data);
  }

  public OutputStream openOutputStream() {
    return new MemoryOutputStream();
  }

  private class MemoryOutputStream extends ByteArrayOutputStream {
    public void close() {
      MemoryFile.this.data = toByteArray();
    }
  }
}
