package org.smoothbuild.object;

import java.util.List;
import java.util.Set;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.err.DuplicatePathError;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;
import org.smoothbuild.type.impl.FileSetBuilderInterface;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FileSetBuilder implements FileSetBuilderInterface {
  private final ObjectsDb objectsDb;
  private final List<File> result;
  private final Set<Path> alreadyAdded;

  public FileSetBuilder(ObjectsDb objectsDb) {
    this.objectsDb = objectsDb;
    this.result = Lists.newArrayList();
    this.alreadyAdded = Sets.newHashSet();
  }

  @Override
  public void add(File file) {
    Path path = file.path();
    if (alreadyAdded.contains(path)) {
      throw new ErrorMessageException(new DuplicatePathError(path));
    } else {
      result.add(file);
      alreadyAdded.add(path);
    }
  }

  @Override
  public boolean contains(Path path) {
    return alreadyAdded.contains(path);
  }

  public FileSet build() {
    return objectsDb.fileSet(result);
  }
}
