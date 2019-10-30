package org.smoothbuild.io.fs.mem;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

public interface MemoryElement {
  public Path name();

  public MemoryDir parent();

  public boolean isFile();

  public boolean isDir();

  public boolean hasChild(Path name);

  public MemoryElement child(Path name);

  public List<Path> childNames();

  public void addChild(MemoryElement element);

  public BufferedSource source() throws IOException;

  public BufferedSink sink();

  public Sink sinkWithoutBuffer();
}
