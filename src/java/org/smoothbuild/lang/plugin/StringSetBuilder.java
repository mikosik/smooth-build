package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.db.value.ValueDb;
import org.smoothbuild.lang.function.value.StringSet;
import org.smoothbuild.lang.function.value.StringValue;

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

  public StringSet build() {
    return valueDb.stringSet(result);
  }
}
