package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.FileSet;
import org.smoothbuild.lang.function.value.Value;

import com.google.common.hash.HashCode;

public class CachedFileSet implements FileSet, Value {
  private final ValueDb valueDb;
  private final HashCode hash;

  public CachedFileSet(ValueDb valueDb, HashCode hash) {
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
