package org.smoothbuild.common.bucket.mem;

import java.io.IOException;
import java.util.List;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;
import org.smoothbuild.common.bucket.base.Path;

public interface MemoryElement {
  public Path name();

  public MemoryDir parent();

  public boolean isFile();

  public boolean isDir();

  public boolean hasChild(Path name);

  public MemoryElement child(Path name);

  public List<Path> childNames();

  public void addChild(MemoryElement elem);

  public long size();

  public BufferedSource source() throws IOException;

  public BufferedSink sink();

  public Sink sinkWithoutBuffer();
}