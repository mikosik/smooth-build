package org.smoothbuild.plugin;

import java.util.List;

import org.smoothbuild.object.ObjectDb;

import com.google.common.collect.Lists;

public class StringSetBuilder {
  private final ObjectDb objectDb;
  private final List<StringValue> result;

  public StringSetBuilder(ObjectDb objectDb) {
    this.objectDb = objectDb;
    this.result = Lists.newArrayList();
  }

  public void add(StringValue string) {
    result.add(string);
  }

  public StringSet build() {
    return objectDb.stringSet(result);
  }
}
