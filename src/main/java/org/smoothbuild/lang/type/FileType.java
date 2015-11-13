package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.STRING;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

public class FileType extends Type {
  protected FileType() {
    super("File", SFile.class);
  }

  @Override
  public Value defaultValue(ValuesDb valuesDb) {
    SString path = (SString) STRING.defaultValue(valuesDb);
    Blob content = (Blob) BLOB.defaultValue(valuesDb);
    return valuesDb.file(path, content);
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
