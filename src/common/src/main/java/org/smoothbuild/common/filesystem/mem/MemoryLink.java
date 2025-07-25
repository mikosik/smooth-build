package org.smoothbuild.common.filesystem.mem;

import java.io.IOException;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.Path;

public final class MemoryLink implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private final MemoryElement target;

  public MemoryLink(MemoryDir parent, Path name, MemoryElement target) {
    this.parent = parent;
    this.name = name;
    this.target = target;
  }

  public MemoryElement target() {
    return target;
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
  public void addChild(MemoryElement elem) throws IOException {
    target.addChild(elem);
  }

  @Override
  public long size() {
    return target.size();
  }

  @Override
  public Source source() throws IOException {
    return target.source();
  }

  @Override
  public Sink sink() throws IOException {
    return target.sink();
  }
}
