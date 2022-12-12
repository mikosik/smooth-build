package org.smoothbuild.run.eval;

import org.smoothbuild.bytecode.expr.value.BlobB;
import org.smoothbuild.bytecode.expr.value.StringB;
import org.smoothbuild.bytecode.expr.value.TupleB;

public class FileStruct {
  public static final String NAME = "File";
  private static final int CONTENT_IDX = 0;
  private static final int PATH_IDX = 1;

  public static BlobB fileContent(TupleB file) {
    return (BlobB) file.get(CONTENT_IDX);
  }

  public static StringB filePath(TupleB file) {
    return (StringB) file.get(PATH_IDX);
  }
}
