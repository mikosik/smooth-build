package org.smoothbuild.run.eval;

import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;

public class FileStruct {
  public static final String NAME = "File";
  private static final int PATH_IDX = 0;
  private static final int CONTENT_IDX = 1;

  public static StringB filePath(TupleB file) {
    return (StringB) file.get(PATH_IDX);
  }

  public static BlobB fileContent(TupleB file) {
    return (BlobB) file.get(CONTENT_IDX);
  }
}
