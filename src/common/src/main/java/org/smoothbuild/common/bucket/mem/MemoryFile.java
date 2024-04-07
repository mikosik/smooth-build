package org.smoothbuild.common.bucket.mem;

import java.io.IOException;
import java.util.List;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSink;
import okio.Sink;
import org.smoothbuild.common.bucket.base.Path;

public final class MemoryFile implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private ByteString data;

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
  public void addChild(MemoryElement elem) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long size() {
    return data.size();
  }

  @Override
  public BufferedSource source() throws IOException {
    if (data == null) {
      throw new IOException("File does not exist");
    }

    return new Buffer().write(data);
  }

  @Override
  public Sink sink() {
    return new MySink();
  }

  private class MySink extends ForwardingSink {
    private boolean closed = false;

    public MySink() {
      super(new Buffer());
    }

    @Override
    public void close() {
      if (!closed) {
        closed = true;
        data = ((Buffer) this.delegate()).readByteString();
      }
    }
  }
}
