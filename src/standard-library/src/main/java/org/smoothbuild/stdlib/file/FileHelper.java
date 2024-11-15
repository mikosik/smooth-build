package org.smoothbuild.stdlib.file;

import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import java.util.HashMap;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class FileHelper {
  public static HashMap<String, BTuple> fileArrayArrayToMap(
      NativeApi nativeApi, BArray fileArrayArray) throws BytecodeException {
    var result = new HashMap<String, BTuple>();
    for (BArray fileArray : fileArrayArray.elements(BArray.class)) {
      if (fileArrayToMap(nativeApi, fileArray, result)) {
        return null;
      }
    }
    return result;
  }

  public static HashMap<String, BTuple> fileArrayToMap(NativeApi nativeApi, BArray fileArray)
      throws BytecodeException {
    var result = new HashMap<String, BTuple>();
    if (fileArrayToMap(nativeApi, fileArray, result)) {
      return null;
    }
    return result;
  }

  private static boolean fileArrayToMap(
      NativeApi nativeApi, BArray fileArray, HashMap<String, BTuple> result)
      throws BytecodeException {
    for (var entry : fileArray.elements(BTuple.class)) {
      var path = filePath(entry).toJavaString();
      if (result.put(path, entry) != null) {
        nativeApi.log().error("File " + path + " is contained by two different library jar files.");
        return true;
      }
    }
    return false;
  }
}
