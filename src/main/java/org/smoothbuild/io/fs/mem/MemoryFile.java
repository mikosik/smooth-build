package org.smoothbuild.io.fs.mem;

import static okio.Okio.buffer;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.io.fs.base.PathS;

import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSink;

public class MemoryFile implements MemoryElement {
  private final MemoryDir parent;
  private final PathS name;
  private ByteString data;

  public MemoryFile(MemoryDir parent, PathS name) {
    this.parent = parent;
    this.name = name;
  }

  @Override
  public PathS name() {
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
  public boolean hasChild(PathS name) {
    return false;
  }

  @Override
  public MemoryElement child(PathS name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<PathS> childNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addChild(MemoryElement elem) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BufferedSource source() throws IOException {
    if (data == null) {
      throw new IOException("File does not exist");
    }

    return new Buffer().write(data);
  }

  @Override
  public BufferedSink sink() {
    return buffer(sinkWithoutBuffer());
  }

  @Override
  public MySink sinkWithoutBuffer() {
    return new MySink();
  }

  private class MySink extends ForwardingSink {
    public MySink() {
      super(new Buffer());
    }

    @Override
    public void close() {
      data = ((Buffer) this.delegate()).readByteString();
    }
  }
}
