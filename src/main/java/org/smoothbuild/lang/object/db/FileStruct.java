package org.smoothbuild.lang.object.db;

import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;

public class FileStruct {
  private static final int CONTENT_INDEX = 0;
  private static final int PATH_INDEX = 1;

  public static SString filePath(Struct file) {
    return (SString) file.get(PATH_INDEX);
  }

  public static Blob fileContent(Struct file) {
    return (Blob) file.get(CONTENT_INDEX);
  }
}
