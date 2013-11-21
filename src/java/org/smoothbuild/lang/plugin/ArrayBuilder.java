package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Value;

import com.google.common.collect.Lists;

public abstract class ArrayBuilder<T extends Value> {
  private final ValueDb valueDb;
  private final List<T> result;

  public ArrayBuilder(ValueDb valueDb) {
    this.valueDb = valueDb;
    this.result = Lists.newArrayList();
  }

  public void add(T elem) {
    result.add(elem);
  }

  public Array<T> build() {
    return buildImpl(valueDb, result);
  }

  protected abstract Array<T> buildImpl(ValueDb valueDb, List<T> elements);
}
