package org.smoothbuild.testing.lang.function.value;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.FileSet;

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
