package org.smoothbuild.testing.type.impl;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.hash.Hash;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class FakeFileSet implements FileSet {
  private final List<File> files = Lists.newArrayList();

  public void add(File file) {
    files.add(file);
  }

  @Override
  public Iterator<File> iterator() {
    return files.iterator();
  }

  @Override
  public HashCode hash() {
    return Hash.function().hashInt(files.hashCode());
  }
}
