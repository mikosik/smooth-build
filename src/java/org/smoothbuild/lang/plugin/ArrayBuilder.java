package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.io.cache.value.ValueReader;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;

import com.google.common.collect.Lists;

public class ArrayBuilder<T extends Value> {
  private final ValueDb valueDb;
  private final Type arrayType;
  private final ValueReader<T> valueReader;
  private final List<T> result;

  public ArrayBuilder(ValueDb valueDb, Type arrayType, ValueReader<T> valueReader) {
    this.valueDb = valueDb;
    this.arrayType = arrayType;
    this.valueReader = valueReader;
    this.result = Lists.newArrayList();
  }

  public ArrayBuilder<T> add(T elem) {
    result.add(elem);
    return this;
  }

  public Array<T> build() {
    return valueDb.array(result, arrayType, valueReader);
  }
}
