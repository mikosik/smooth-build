package org.smoothbuild.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MemoryLink implements MemoryElement {
  private final MemoryDirectory parent;
  private final String name;
  private final MemoryElement target;

  public MemoryLink(MemoryDirectory parent, String name, MemoryElement target) {
    this.parent = parent;
    this.name = name;
    this.target = target;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public MemoryDirectory parent() {
    return parent;
  }

  @Override
  public boolean isFile() {
    return target.isFile();
  }

  @Override
  public boolean isDirectory() {
    return target.isDirectory();
  }

  @Override
  public boolean hasChild(String name) {
    return target.hasChild(name);
  }

  @Override
  public MemoryElement child(String name) {
    return target.child(name);
  }

  @Override
  public List<String> childNames() {
    return target.childNames();
  }

  @Override
  public void addChild(MemoryElement element) {
    target.addChild(element);
  }

  @Override
  public InputStream createInputStream() {
    return target.createInputStream();
  }

  @Override
  public OutputStream createOutputStream() {
    return target.createOutputStream();
  }
}
