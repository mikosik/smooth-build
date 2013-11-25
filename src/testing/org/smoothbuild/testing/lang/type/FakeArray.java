package org.smoothbuild.testing.lang.type;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.io.cache.value.instance.CachedValue;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SType;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class FakeArray extends CachedValue implements SArray<SFile> {
  private final List<SFile> files = Lists.newArrayList();

  public FakeArray(SType<?> type) {
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
