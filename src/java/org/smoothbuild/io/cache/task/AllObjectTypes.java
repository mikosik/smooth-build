package org.smoothbuild.io.cache.task;

import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;

import org.smoothbuild.io.cache.hash.EnumValues;
import org.smoothbuild.lang.type.SType;

public class AllObjectTypes extends EnumValues<SType<?>> {
  public static final AllObjectTypes INSTANCE = new AllObjectTypes();

  private AllObjectTypes() {
    super(STRING, STRING_ARRAY, FILE, FILE_ARRAY, BLOB, BLOB_ARRAY);
  }
}
