package org.smoothbuild.testing.lang.type;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.value.AbstractValue;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class FakeArray extends AbstractValue implements SArray<SFile> {
  private final List<SFile> files = Lists.newArrayList();

  public FakeArray(Type<?> type) {
    super(type, HashCode.fromInt(0));
  }

  public void add(SFile file) {
    files.add(file);
  }

  @Override
  public Iterator<SFile> iterator() {
    return files.iterator();
  }

  @Override
  public HashCode hash() {
    return Hash.function().hashInt(files.hashCode());
  }
}
