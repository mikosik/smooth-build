package org.smoothbuild.db;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public class FileSetObject implements FileSet, Value {
  private final ValueDb valueDb;
  private final HashCode hash;

  public FileSetObject(ValueDb valueDb, HashCode hash) {
    this.valueDb = checkNotNull(valueDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public Iterator<File> iterator() {
    return valueDb.fileSetIterable(hash).iterator();
  }
}
