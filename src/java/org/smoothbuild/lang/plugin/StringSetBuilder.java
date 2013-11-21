package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.StringValue;

import com.google.common.collect.Lists;

public class StringSetBuilder {
  private final ValueDb valueDb;
  private final List<StringValue> result;

  public StringSetBuilder(ValueDb valueDb) {
    this.valueDb = valueDb;
    this.result = Lists.newArrayList();
  }

  public void add(StringValue string) {
    result.add(string);
  }

  public Array<StringValue> build() {
    return valueDb.stringSet(result);
  }
}
