package org.smoothbuild.io.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class MemoryDir implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private final Map<Path, MemoryElement> map = Maps.newHashMap();

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
    return ImmutableList.copyOf(map.keySet());
  }

  @Override
  public void addChild(MemoryElement element) {
    Path elementName = element.name();
    if (map.containsKey(elementName)) {
      throw new IllegalStateException("Dir already contains child with name '" + elementName
          + "'.");
    }
    map.put(elementName, element);
  }

  public void removeChild(MemoryElement element) {
    map.remove(element.name());
  }

  public void removeAllChildren() {
    map.clear();
  }

  @Override
  public InputStream openInputStream() {
    throw new UnsupportedOperationException();
  }

  @Override
  public OutputStream openOutputStream() {
    throw new UnsupportedOperationException();
  }
}
