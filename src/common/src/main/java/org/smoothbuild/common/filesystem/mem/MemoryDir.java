package org.smoothbuild.common.filesystem.mem;

import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.HashMap;
import java.util.Map;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.Path;

public class MemoryDir implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private final Map<Path, MemoryElement> map = new HashMap<>();

  public MemoryDir(MemoryDir parent, Path name) {
    this.parent = parent;
    this.name = name;
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
    return false;
  }

  @Override
  public boolean isDir() {
    return true;
  }

  @Override
  public boolean hasChild(Path name) {
    return map.containsKey(name);
  }

  @Override
  public MemoryElement child(Path name) {
    MemoryElement result = map.get(name);
    if (result == null) {
      throw new IllegalArgumentException("Element '" + name + "' does not exist.");
    }
    return result;
  }

  @Override
  public List<Path> childNames() {
    return listOfAll(map.keySet());
  }

  @Override
  public void addChild(MemoryElement elem) {
    Path elemName = elem.name();
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
