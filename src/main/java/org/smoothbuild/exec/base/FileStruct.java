package org.smoothbuild.exec.base;

import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.db.object.obj.val.TupleB;

public class FileStruct {
  public static final String NAME = "File";
  private static final int CONTENT_INDEX = 0;
  private static final int PATH_INDEX = 1;
  public static final String CONTENT_FIELD_NAME = "content";
  public static final String PATH_FIELD_NAME = "path";

  public static StringB filePath(TupleB file) {
    return (StringB) file.get(PATH_INDEX);
  }

  public static BlobB fileContent(TupleB file) {
    return (BlobB) file.get(CONTENT_INDEX);
  }
}
