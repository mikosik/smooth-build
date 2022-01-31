package org.smoothbuild.io.fs.mem;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.io.fs.base.PathS;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

public interface MemoryElement {
  public PathS name();

  public MemoryDir parent();

  public boolean isFile();

  public boolean isDir();

  public boolean hasChild(PathS name);

  public MemoryElement child(PathS name);

  public List<PathS> childNames();

  public void addChild(MemoryElement elem);

  public BufferedSource source() throws IOException;

  public BufferedSink sink();

  public Sink sinkWithoutBuffer();
}
