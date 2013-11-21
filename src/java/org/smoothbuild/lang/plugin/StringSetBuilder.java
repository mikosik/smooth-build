package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.StringValue;

public class StringSetBuilder extends ArrayBuilder<StringValue> {
  public StringSetBuilder(ValueDb valueDb) {
    super(valueDb);
  }

  @Override
  protected Array<StringValue> buildImpl(ValueDb valueDb, List<StringValue> elements) {
    return valueDb.stringSet(elements);
  }
}
