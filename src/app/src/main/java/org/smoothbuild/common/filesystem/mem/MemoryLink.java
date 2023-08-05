package org.smoothbuild.common.filesystem.mem;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.common.filesystem.base.PathS;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

public class MemoryLink implements MemoryElement {
  private final MemoryDir parent;
  private final PathS name;
  private final MemoryElement target;

  public MemoryLink(MemoryDir parent, PathS name, MemoryElement target) {
    this.parent = parent;
    this.name = name;
    this.target = target;
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
    return target.isFile();
  }

  @Override
  public boolean isDir() {
    return target.isDir();
  }

  @Override
  public boolean hasChild(PathS name) {
    return target.hasChild(name);
  }

  @Override
  public MemoryElement child(PathS name) {
    return target.child(name);
  }

  @Override
  public List<PathS> childNames() {
    return target.childNames();
  }

  @Override
  public void addChild(MemoryElement elem) {
    target.addChild(elem);
  }

  @Override
  public long size() {
    return target.size();
  }

  @Override
  public BufferedSource source() throws IOException {
    return target.source();
  }

  @Override
  public BufferedSink sink() {
    return target.sink();
  }

  @Override
  public Sink sinkWithoutBuffer() {
    return target.sinkWithoutBuffer();
  }
}
