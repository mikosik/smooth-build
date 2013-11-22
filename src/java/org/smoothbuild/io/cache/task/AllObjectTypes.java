package org.smoothbuild.io.cache.task;

import static org.smoothbuild.lang.type.Type.BLOB;
import static org.smoothbuild.lang.type.Type.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Type.FILE;
import static org.smoothbuild.lang.type.Type.FILE_ARRAY;
import static org.smoothbuild.lang.type.Type.STRING;
import static org.smoothbuild.lang.type.Type.STRING_ARRAY;

import org.smoothbuild.io.cache.hash.EnumValues;
import org.smoothbuild.lang.type.Type;

public class AllObjectTypes extends EnumValues<Type> {
  public static final AllObjectTypes INSTANCE = new AllObjectTypes();

  private AllObjectTypes() {
    super(STRING, STRING_ARRAY, FILE, FILE_ARRAY, BLOB, BLOB_ARRAY);
  }
}
