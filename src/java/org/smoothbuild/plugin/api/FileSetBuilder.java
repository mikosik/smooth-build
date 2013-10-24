package org.smoothbuild.plugin.api;

import java.util.List;
import java.util.Set;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.ObjectDb;
import org.smoothbuild.plugin.api.err.CannotAddDuplicatePathError;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FileSetBuilder {
  private final ObjectDb objectDb;
  private final List<File> result;
  private final Set<Path> alreadyAdded;

  public FileSetBuilder(ObjectDb objectDb) {
    this.objectDb = objectDb;
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

  public FileSet build() {
    return objectDb.fileSet(result);
  }
}
