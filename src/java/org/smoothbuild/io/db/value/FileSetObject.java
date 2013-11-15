package org.smoothbuild.io.db.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.plugin.File;
import org.smoothbuild.lang.plugin.FileSet;
import org.smoothbuild.lang.plugin.Value;

import com.google.common.hash.HashCode;

public class FileSetObject implements FileSet, Value {
  private final ValueDb valueDb;
  private final HashCode hash;

  public FileSetObject(ValueDb valueDb, HashCode hash) {
    this.valueDb = checkNotNull(valueDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public Type type() {
    return Type.FILE_SET;
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
