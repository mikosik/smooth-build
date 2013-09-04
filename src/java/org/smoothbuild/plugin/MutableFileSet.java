package org.smoothbuild.plugin;

import java.util.Iterator;
import java.util.Map;


import com.google.common.collect.Maps;

public class MutableFileSet implements FileSet {
  private final Map<Path, File> map = Maps.newHashMap();

  @Override
  public Iterator<File> iterator() {
    return map.values().iterator();
  }

  public boolean contains(Path path) {
    return map.containsKey(path);
  }

  @Override
  public File file(Path path) {
    File file = map.get(path);
    if (file == null) {
      throw new IllegalArgumentException("File " + path + " does not exist.");
    }
    return file;
  }

  public void add(File file) {
    Path path = file.path();
    if (map.containsKey(path)) {
      throw new IllegalArgumentException("FileSet already contains " + path);
    }
    map.put(path, file);
  }

  @Override
  public File createFile(Path path) {
    // TODO remove once it is removed from FileSet interface
    throw new UnsupportedOperationException();
  }
}
