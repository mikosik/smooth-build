package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;

public class LambdaStruct {
  private static final int NAME_INDEX = 0;
  private static final int MODULE_HASH_INDEX = 1;

  public static Str name(Struc_ lambda) {
    return (Str) lambda.get(NAME_INDEX);
  }

  public static Blob moduleHash(Struc_ lambda) {
    return (Blob) lambda.get(MODULE_HASH_INDEX);
  }
}
