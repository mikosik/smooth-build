package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;

public class FileStruct {
  public static final String NAME = "File";
  private static final int CONTENT_INDEX = 0;
  private static final int PATH_INDEX = 1;
  public static final String CONTENT_FIELD_NAME = "content";
  public static final String PATH_FIELD_NAME = "path";

  public static StringH filePath(TupleH file) {
    return (StringH) file.get(PATH_INDEX);
  }

  public static BlobH fileContent(TupleH file) {
    return (BlobH) file.get(CONTENT_INDEX);
  }
}
