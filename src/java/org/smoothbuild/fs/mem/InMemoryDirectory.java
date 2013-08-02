package org.smoothbuild.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class InMemoryDirectory implements InMemoryElement {
  private final String name;
  private final Map<String, InMemoryElement> map = Maps.newHashMap();

  public InMemoryDirectory(String name) {
    this.name = name;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public boolean isFile() {
    return false;
  }

  @Override
  public boolean isDirectory() {
    return true;
  }

  @Override
  public boolean hasChild(String name) {
    return map.containsKey(name);
  }

  @Override
  public InMemoryElement child(String name) {
    InMemoryElement result = map.get(name);
    if (result == null) {
      throw new IllegalArgumentException("Element '" + name + "' does not exist.");
    }
    return result;
  }

  @Override
  public List<String> childNames() {
    return ImmutableList.copyOf(map.keySet());
  }

  @Override
  public void addChild(InMemoryElement element) {
    String elementName = element.name();
    if (map.containsKey(elementName)) {
      throw new IllegalStateException("Directory already contains child with name '" + elementName
          + "'.");
    }
    map.put(elementName, element);
  }

  @Override
  public InputStream createInputStream() {
    throw new UnsupportedOperationException();
  }

  @Override
  public OutputStream createOutputStream() {
    throw new UnsupportedOperationException();
  }
}
