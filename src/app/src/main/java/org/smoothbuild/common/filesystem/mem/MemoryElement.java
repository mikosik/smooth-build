package org.smoothbuild.common.filesystem.mem;

import java.io.IOException;
import java.util.List;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;
import org.smoothbuild.common.filesystem.base.PathS;

public interface MemoryElement {
  public PathS name();

  public MemoryDir parent();

  public boolean isFile();

  public boolean isDir();

  public boolean hasChild(PathS name);

  public MemoryElement child(PathS name);

  public List<PathS> childNames();

  public void addChild(MemoryElement elem);

  public long size();

  public BufferedSource source() throws IOException;

  public BufferedSink sink();

  public Sink sinkWithoutBuffer();
}
