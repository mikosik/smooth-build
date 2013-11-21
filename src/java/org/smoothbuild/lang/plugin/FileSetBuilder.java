package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;

public class FileSetBuilder extends ArrayBuilder<File> {
  public FileSetBuilder(ValueDb valueDb) {
    super(valueDb);
  }

  @Override
  protected Array<File> buildImpl(ValueDb valueDb, List<File> elements) {
    return valueDb.fileSet(elements);
  }
}
