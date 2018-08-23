package org.smoothbuild.io.fs.mem;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.io.fs.base.Path;

import okio.BufferedSink;
import okio.BufferedSource;

public class MemoryLink implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private final MemoryElement target;

  public MemoryLink(MemoryDir parent, Path name, MemoryElement target) {
    this.parent = parent;
    this.name = name;
    this.target = target;
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
    return target.isFile();
  }

  @Override
  public boolean isDir() {
    return target.isDir();
  }

  @Override
  public boolean hasChild(Path name) {
    return target.hasChild(name);
  }

  @Override
  public MemoryElement child(Path name) {
    return target.child(name);
  }

  @Override
  public List<Path> childNames() {
    return target.childNames();
  }

  @Override
  public void addChild(MemoryElement element) {
    target.addChild(element);
  }

  @Override
  public BufferedSource source() throws IOException {
    return target.source();
  }

  @Override
  public BufferedSink sink() {
    return target.sink();
  }
}
