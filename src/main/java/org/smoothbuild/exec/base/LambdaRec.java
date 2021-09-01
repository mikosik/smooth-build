package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;

public class LambdaRec {
  private static final int NAME_INDEX = 0;
  private static final int MODULE_HASH_INDEX = 1;

  public static Str name(Rec lambda) {
    return (Str) lambda.get(NAME_INDEX);
  }

  public static Blob moduleHash(Rec lambda) {
    return (Blob) lambda.get(MODULE_HASH_INDEX);
  }
}
