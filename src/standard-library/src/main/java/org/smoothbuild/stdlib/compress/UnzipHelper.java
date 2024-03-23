package org.smoothbuild.stdlib.compress;

import static org.smoothbuild.stdlib.java.UnjarFunc.JAR_MANIFEST_PATH;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;
import static org.smoothbuild.virtualmachine.evaluate.plugin.UnzipBlob.unzipBlob;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class UnzipHelper {
  private static final Predicate<String> NOT_MANIFEST_PREDICATE = f -> !f.equals(JAR_MANIFEST_PATH);

  public static Map<String, BTuple> filesFromLibJars(NativeApi nativeApi, BArray libJars)
      throws BytecodeException {
    return filesFromLibJars(nativeApi, libJars, NOT_MANIFEST_PREDICATE);
  }

  public static HashMap<String, BTuple> filesFromLibJars(
      NativeApi nativeApi, BArray libJars, Predicate<String> filter) throws BytecodeException {
    var result = new HashMap<String, BTuple>();
    var jars = libJars.elements(BTuple.class);
    for (int i = 0; i < jars.size(); i++) {
      var jarFile = jars.get(i);
      var classes = filesFromJar(nativeApi, jarFile, filter);
      if (classes == null) {
        return null;
      }
      for (var entry : classes.entrySet()) {
        var path = entry.getKey();
        if (result.put(path, entry.getValue()) != null) {
          nativeApi
              .log()
              .error("File " + path + " is contained by two different library jar files.");
          return null;
        }
      }
    }
    return result;
  }

  public static Map<String, BTuple> filesFromJar(NativeApi nativeApi, BTuple jarFile)
      throws BytecodeException {
    return filesFromJar(nativeApi, jarFile, NOT_MANIFEST_PREDICATE);
  }

  private static Map<String, BTuple> filesFromJar(
      NativeApi nativeApi, BTuple jarFile, Predicate<String> filter) throws BytecodeException {
    var files = unzipToArrayB(nativeApi, fileContent(jarFile), filter);
    if (files == null) {
      return null;
    }
    return files.elements(BTuple.class).toMap(f -> filePath(f).toJavaString(), x -> x);
  }

  public static BArray unzipToArrayB(
      NativeApi nativeApi, BBlob blob, Predicate<String> includePredicate)
      throws BytecodeException {
    return unzipBlob(nativeApi.factory(), blob, includePredicate)
        .ifLeft(error -> nativeApi.log().error("Error reading archive: " + error))
        .rightOrGet(() -> null);
  }
}
