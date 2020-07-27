package org.smoothbuild.record.db;

import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.RString;
import org.smoothbuild.record.base.Tuple;

public class FileStruct {
  private static final int CONTENT_INDEX = 0;
  private static final int PATH_INDEX = 1;

  public static RString filePath(Tuple file) {
    return (RString) file.get(PATH_INDEX);
  }

  public static Blob fileContent(Tuple file) {
    return (Blob) file.get(CONTENT_INDEX);
  }
}
