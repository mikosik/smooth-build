package org.smoothbuild.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface MemoryElement {
  public String name();

  public boolean isFile();

  public boolean isDirectory();

  public boolean hasChild(String name);

  public MemoryElement child(String name);

  public List<String> childNames();

  public void addChild(MemoryElement newDir);

  public InputStream createInputStream();

  public OutputStream createOutputStream();
}
