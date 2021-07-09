package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;

public class FunctionTuple {
  private static final int NAME_INDEX = 0;
  private static final int MODULE_HASH_INDEX = 1;

  public static Str name(Tuple function) {
    return (Str) function.get(NAME_INDEX);
  }

  public static Blob moduleHash(Tuple function) {
    return (Blob) function.get(MODULE_HASH_INDEX);
  }
}
