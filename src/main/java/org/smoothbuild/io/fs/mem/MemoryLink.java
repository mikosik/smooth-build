package org.smoothbuild.io.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.smoothbuild.io.fs.base.Path;

public class MemoryLink implements MemoryElement {
  private final MemoryDirectory parent;
  private final Path name;
  private final MemoryElement target;

  public MemoryLink(MemoryDirectory parent, Path name, MemoryElement target) {
    this.parent = parent;
    this.name = name;
    this.target = target;
  }

  @Override
  public Path name() {
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
  public InputStream createInputStream() {
    return target.createInputStream();
  }

  @Override
  public OutputStream createOutputStream() {
    return target.createOutputStream();
  }
}
