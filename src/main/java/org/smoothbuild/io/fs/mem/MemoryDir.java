package org.smoothbuild.io.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.collect.ImmutableList;

public class MemoryDir implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private final Map<Path, MemoryElement> map = new HashMap<>();

  public MemoryDir(MemoryDir parent, Path name) {
    this.parent = parent;
    this.name = name;
  }

  public Path name() {
    return name;
  }

  public MemoryDir parent() {
    return parent;
  }

  public boolean isFile() {
    return false;
  }

  public boolean isDir() {
    return true;
  }

  public boolean hasChild(Path name) {
    return map.containsKey(name);
  }

  public MemoryElement child(Path name) {
    MemoryElement result = map.get(name);
    if (result == null) {
      throw new IllegalArgumentException("Element '" + name + "' does not exist.");
    }
    return result;
  }

  public List<Path> childNames() {
    return ImmutableList.copyOf(map.keySet());
  }

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

  public InputStream openInputStream() {
    throw new UnsupportedOperationException();
  }

  public OutputStream openOutputStream() {
    throw new UnsupportedOperationException();
  }
}
