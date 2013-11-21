package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;

import com.google.common.collect.Lists;

public class FileSetBuilder {
  private final ValueDb valueDb;
  private final List<File> result;

  public FileSetBuilder(ValueDb valueDb) {
    this.valueDb = valueDb;
    this.result = Lists.newArrayList();
  }

  public void add(File file) {
    result.add(file);
  }

  public Array<File> build() {
    return valueDb.fileSet(result);
  }
}
