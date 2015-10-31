package org.smoothbuild.lang.type;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.Value;

public class FileType extends Type {
  protected FileType() {
    super("File", SFile.class);
  }

  @Override
  public Value defaultValue(ValuesDb valuesDb) {
    return valuesDb.file(path("."), (Blob) Types.BLOB.defaultValue(valuesDb));
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
