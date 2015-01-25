package org.smoothbuild.lang.type;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.Value;

public class FileType extends Type {
  protected FileType() {
    super("File", SFile.class);
  }

  @Override
  public Value defaultValue(ObjectsDb objectsDb) {
    return objectsDb.file(path("."), (Blob) Types.BLOB.defaultValue(objectsDb));
  }

  @Override
  public boolean isAllowedAsResult() {
    return true;
  }

  @Override
  public boolean isAllowedAsParameter() {
    return true;
  }
}
