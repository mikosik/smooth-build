package org.smoothbuild.common.filesystem.mem;

import static org.smoothbuild.common.collect.List.listOfAll;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.Sink;
import okio.Source;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.Path;

public final class MemoryDir implements MemoryElement {
  private final @Nullable MemoryDir parent;
  private final Path name;
  private final Map<Path, MemoryElement> map = new HashMap<>();

  public MemoryDir(@Nullable MemoryDir parent, Path name) {
    this.parent = parent;
    this.name = name;
  }

  @Override
  public Path name() {
    return name;
  }

  @Override
  public MemoryDir parent() {
    return Preconditions.checkNotNull(parent, "Dir " + name.q() + " does not have parent.");
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
  public void addChild(MemoryElement elem) throws IOException {
    Path elemName = elem.name();
    if (map.containsKey(elemName)) {
      throw new IOException("Dir already contains child with name '" + elemName + "'.");
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
  public Source source() throws IOException {
    throw new IOException();
  }

  @Override
  public Sink sink() throws IOException {
    throw new IOException();
  }
}
