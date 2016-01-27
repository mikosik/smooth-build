package org.smoothbuild.io.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.smoothbuild.io.fs.base.Path;

public class MemoryLink implements MemoryElement {
  private final MemoryDir parent;
  private final Path name;
  private final MemoryElement target;

  public MemoryLink(MemoryDir parent, Path name, MemoryElement target) {
    this.parent = parent;
    this.name = name;
    this.target = target;
  }

  public Path name() {
    return name;
  }

  public MemoryDir parent() {
    return parent;
  }

  public boolean isFile() {
    return target.isFile();
  }

  public boolean isDir() {
    return target.isDir();
  }

  public boolean hasChild(Path name) {
    return target.hasChild(name);
  }

  public MemoryElement child(Path name) {
    return target.child(name);
  }

  public List<Path> childNames() {
    return target.childNames();
  }

  public void addChild(MemoryElement element) {
    target.addChild(element);
  }

  public InputStream openInputStream() {
    return target.openInputStream();
  }

  public OutputStream openOutputStream() {
    return target.openOutputStream();
  }
}
