package org.smoothbuild.virtualmachine.bytecode.helper;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;

public class FileStruct {
  private static final int CONTENT_IDX = 0;
  private static final int PATH_IDX = 1;

  public static BBlob fileContent(BTuple file) throws BytecodeException {
    return (BBlob) file.get(CONTENT_IDX);
  }

  public static BString filePath(BTuple file) throws BytecodeException {
    return (BString) file.get(PATH_IDX);
  }
}
