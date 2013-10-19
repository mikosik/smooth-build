package org.smoothbuild.object;

import java.io.OutputStream;
import java.util.Set;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.err.DuplicatePathError;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;
import org.smoothbuild.type.api.MutableFileSet;
import org.smoothbuild.type.impl.FileSetBuilderInterface;
import org.smoothbuild.type.impl.MutableStoredFileSet;

import com.google.common.collect.Sets;

public class FileSetBuilder implements FileSetBuilderInterface {
  private final MutableFileSet fileSet;
  private final Set<Path> alreadyAdded;

  public FileSetBuilder(FileSystem fileSystem) {
    this(new MutableStoredFileSet(fileSystem));
  }

  // TODO remove once migration away from MutableStoredFile is complete
  public FileSetBuilder(MutableFileSet fileSet) {
    this.fileSet = fileSet;
    this.alreadyAdded = Sets.newHashSet();
  }

  @Override
  public void add(File file) {
    Path path = file.path();
    if (alreadyAdded.contains(path)) {
      throw new ErrorMessageException(new DuplicatePathError(path));
    } else {
      fileSet.createFile(path).setContent(file);
      alreadyAdded.add(path);
    }
  }

  @Override
  public OutputStream openFileOutputStream(Path path) {
    // TODO each opened OutputStrea should add "path->null" entry to some map.
    // Closing OutputStream should assert that entry still maps to null and
    // change mapping to File.hash().
    alreadyAdded.add(path);
    return fileSet.openFileOutputStream(path);
  }

  @Override
  public boolean contains(Path path) {
    return alreadyAdded.contains(path);
  }

  public FileSet build() {
    return fileSet;
  }
}
