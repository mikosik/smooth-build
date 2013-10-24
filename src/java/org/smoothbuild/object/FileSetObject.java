package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;

import com.google.common.hash.HashCode;

public class FileSetObject implements FileSet, Hashed {
  private final ObjectDb objectDb;
  private final HashCode hash;

  public FileSetObject(ObjectDb objectDb, HashCode hash) {
    this.objectDb = checkNotNull(objectDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public Iterator<File> iterator() {
    return objectDb.fileSetIterable(hash).iterator();
  }
}
