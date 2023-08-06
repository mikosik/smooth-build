package org.smoothbuild.virtualmachine.bytecode.helper;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;

public class FileStruct {
  public static final String NAME = "File";
  private static final int CONTENT_IDX = 0;
  private static final int PATH_IDX = 1;

  public static BlobB fileContent(TupleB file) throws BytecodeException {
    return (BlobB) file.get(CONTENT_IDX);
  }

  public static StringB filePath(TupleB file) throws BytecodeException {
    return (StringB) file.get(PATH_IDX);
  }
}
