package org.smoothbuild.testing.lang.plugin;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.db.hash.Hash;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.plugin.File;
import org.smoothbuild.lang.plugin.FileSet;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class FakeFileSet implements FileSet {
  private final List<File> files = Lists.newArrayList();

  @Override
  public Type type() {
    return Type.FILE_SET;
  }

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
