package org.smoothbuild.testing.lang.type;

import static org.smoothbuild.lang.type.Type.FILE_ARRAY;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.value.AbstractValue;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class FakeFileSet extends AbstractValue implements Array<File> {
  private final List<File> files = Lists.newArrayList();

  public FakeFileSet() {
    super(FILE_ARRAY, HashCode.fromInt(0));
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
