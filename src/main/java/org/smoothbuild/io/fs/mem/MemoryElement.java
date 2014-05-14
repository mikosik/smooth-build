package org.smoothbuild.io.fs.mem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.smoothbuild.io.fs.base.Path;

public interface MemoryElement {
  public Path name();

  public MemoryDirectory parent();

  public boolean isFile();

  public boolean isDirectory();

  public boolean hasChild(Path name);

  public MemoryElement child(Path name);

  public List<Path> childNames();

  public void addChild(MemoryElement element);

  public InputStream openInputStream();

  public OutputStream openOutputStream();
}
