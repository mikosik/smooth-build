package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.hash.HashCode;

public class FileSetObject implements FileSet, Hashed {
  private final ObjectsDb objectsDb;
  private final HashCode hash;

  public FileSetObject(ObjectsDb objectsDb, HashCode hash) {
    this.objectsDb = checkNotNull(objectsDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public Iterator<File> iterator() {
    return objectsDb.fileSetIterable(hash).iterator();
  }
}
