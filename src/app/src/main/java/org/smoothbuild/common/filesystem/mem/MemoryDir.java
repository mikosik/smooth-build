package org.smoothbuild.common.filesystem.mem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.common.filesystem.base.PathS;

import com.google.common.collect.ImmutableList;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

public class MemoryDir implements MemoryElement {
  private final MemoryDir parent;
  private final PathS name;
  private final Map<PathS, MemoryElement> map = new HashMap<>();

  public MemoryDir(MemoryDir parent, PathS name) {
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
    return false;
  }

  @Override
  public boolean isDir() {
    return true;
  }

  @Override
  public boolean hasChild(PathS name) {
    return map.containsKey(name);
  }

  @Override
  public MemoryElement child(PathS name) {
    MemoryElement result = map.get(name);
    if (result == null) {
      throw new IllegalArgumentException("Element '" + name + "' does not exist.");
    }
    return result;
  }

  @Override
  public List<PathS> childNames() {
    return ImmutableList.copyOf(map.keySet());
  }

  @Override
  public void addChild(MemoryElement elem) {
    PathS elemName = elem.name();
    if (map.containsKey(elemName)) {
      throw new IllegalStateException("Dir already contains child with name '" + elemName + "'.");
    }
    map.put(elemName, elem);
  }

  public void removeChild(MemoryElement elem) {
    map.remove(elem.name());
  }

  public void removeAllChildren() {
    map.clear();
  }

  @Override
  public long size() {
    return 0;
  }

  @Override
  public BufferedSource source() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BufferedSink sink() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Sink sinkWithoutBuffer() {
    throw new UnsupportedOperationException();
  }
}
