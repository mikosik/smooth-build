package org.smoothbuild.io.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface MemoryElement {
  public String name();

  public MemoryDirectory parent();

  public boolean isFile();

  public boolean isDirectory();

  public boolean hasChild(String name);

  public MemoryElement child(String name);

  public List<String> childNames();

  public void addChild(MemoryElement element);

  public InputStream createInputStream();

  public OutputStream createOutputStream();
}
