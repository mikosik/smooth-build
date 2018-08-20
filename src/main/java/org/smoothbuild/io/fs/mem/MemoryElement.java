package org.smoothbuild.io.fs.mem;

import java.util.List;

import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSink;
import okio.BufferedSource;

public interface MemoryElement {
  public Path name();

  public MemoryDir parent();

  public boolean isFile();

  public boolean isDir();

  public boolean hasChild(Path name);

  public MemoryElement child(Path name);

  public List<Path> childNames();

  public void addChild(MemoryElement element);

  public BufferedSource source();

  public BufferedSink sink();
}
