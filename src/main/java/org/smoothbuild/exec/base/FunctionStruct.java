package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;

public class FunctionStruct {
  private static final int NAME_INDEX = 0;
  private static final int MODULE_HASH_INDEX = 1;

  public static StringH name(TupleH function) {
    return (StringH) function.get(NAME_INDEX);
  }

  public static BlobH moduleHash(TupleH function) {
    return (BlobH) function.get(MODULE_HASH_INDEX);
  }
}
