package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;

public class FileStruct {
  public static final String NAME = "File";
  private static final int CONTENT_INDEX = 0;
  private static final int PATH_INDEX = 1;

  public static Str filePath(Tuple file) {
    return (Str) file.get(PATH_INDEX);
  }

  public static Blob fileContent(Tuple file) {
    return (Blob) file.get(CONTENT_INDEX);
  }
}
