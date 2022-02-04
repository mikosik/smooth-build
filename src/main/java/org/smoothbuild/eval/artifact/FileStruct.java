package org.smoothbuild.eval.artifact;

import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;

public class FileStruct {
  public static final String NAME = "File";
  private static final int PATH_IDX = 0;
  private static final int CONTENT_IDX = 1;
  public static final String PATH_FIELD_NAME = "path";
  public static final String CONTENT_FIELD_NAME = "content";

  public static StringB filePath(TupleB file) {
    return (StringB) file.get(PATH_IDX);
  }

  public static BlobB fileContent(TupleB file) {
    return (BlobB) file.get(CONTENT_IDX);
  }
}
