package org.smoothbuild.common.filesystem.mem;

import java.io.IOException;
import java.util.List;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.filesystem.base.Path;

public sealed interface MemoryElement permits MemoryDir, MemoryFile, MemoryLink {
  public Path name();

  public MemoryDir parent();

  public boolean isFile();

  public boolean isDir();

  public boolean hasChild(Path name);

  public MemoryElement child(Path name);

  public List<Path> childNames();

  public void addChild(MemoryElement elem);

  public long size();

  public Source source() throws IOException;

  public Sink sink() throws IOException;
}
