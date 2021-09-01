package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;

public class FileStruct {
  public static final String NAME = "File";
  private static final int CONTENT_INDEX = 0;
  private static final int PATH_INDEX = 1;

  public static Str filePath(Rec file) {
    return (Str) file.get(PATH_INDEX);
  }

  public static Blob fileContent(Rec file) {
    return (Blob) file.get(CONTENT_INDEX);
  }
}
