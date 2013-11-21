package org.smoothbuild.lang.plugin;

import java.util.List;
import java.util.Set;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.err.CannotAddDuplicatePathError;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FileSetBuilder {
  private final ValueDb valueDb;
  private final List<File> result;
  private final Set<Path> alreadyAdded;

  public FileSetBuilder(ValueDb valueDb) {
    this.valueDb = valueDb;
    this.result = Lists.newArrayList();
    this.alreadyAdded = Sets.newHashSet();
  }

  public void add(File file) {
    Path path = file.path();
    if (alreadyAdded.contains(path)) {
      throw new ErrorMessageException(new CannotAddDuplicatePathError(path));
    } else {
      result.add(file);
      alreadyAdded.add(path);
    }
  }

  public boolean contains(Path path) {
    return alreadyAdded.contains(path);
  }

  public Array<File> build() {
    return valueDb.fileSet(result);
  }
}
