package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;

public class NativeCodeTuple {
  private static final int METHOD_PATH_INDEX = 0;
  private static final int CONTENT_INDEX = 1;

  public static Str methodPath(Tuple nativeCode) {
    return (Str) nativeCode.get(METHOD_PATH_INDEX);
  }

  public static Blob content(Tuple nativeCode) {
    return (Blob) nativeCode.get(CONTENT_INDEX);
  }
}
