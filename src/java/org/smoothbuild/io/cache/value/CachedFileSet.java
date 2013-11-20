package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;

import java.util.Iterator;

import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.FileSet;

import com.google.common.hash.HashCode;

public class CachedFileSet extends AbstractValue implements FileSet {
  private final ValueDb valueDb;

  public CachedFileSet(ValueDb valueDb, HashCode hash) {
    super(FILE_SET, hash);
    this.valueDb = checkNotNull(valueDb);
  }

  @Override
  public Iterator<File> iterator() {
    return valueDb.fileSetIterable(hash()).iterator();
  }
}
