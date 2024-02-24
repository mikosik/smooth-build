package org.smoothbuild.stdlib.compress;

import static org.smoothbuild.run.eval.FileStruct.fileContent;
import static org.smoothbuild.run.eval.FileStruct.filePath;
import static org.smoothbuild.stdlib.java.UnjarFunc.JAR_MANIFEST_PATH;
import static org.smoothbuild.vm.evaluate.plugin.UnzipBlob.unzipBlob;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class UnzipHelper {
  private static final Predicate<String> NOT_MANIFEST_PREDICATE = f -> !f.equals(JAR_MANIFEST_PATH);

  public static Map<String, TupleB> filesFromLibJars(NativeApi nativeApi, ArrayB libJars)
      throws BytecodeException {
    return filesFromLibJars(nativeApi, libJars, NOT_MANIFEST_PREDICATE);
  }

  public static HashMap<String, TupleB> filesFromLibJars(
      NativeApi nativeApi, ArrayB libJars, Predicate<String> filter) throws BytecodeException {
    var result = new HashMap<String, TupleB>();
    var jars = libJars.elems(TupleB.class);
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

  public static Map<String, TupleB> filesFromJar(NativeApi nativeApi, TupleB jarFile)
      throws BytecodeException {
    return filesFromJar(nativeApi, jarFile, NOT_MANIFEST_PREDICATE);
  }

  private static Map<String, TupleB> filesFromJar(
      NativeApi nativeApi, TupleB jarFile, Predicate<String> filter) throws BytecodeException {
    var files = unzipToArrayB(nativeApi, fileContent(jarFile), filter);
    if (files == null) {
      return null;
    }
    return files.elems(TupleB.class).toMap(f -> filePath(f).toJ(), x -> x);
  }

  public static ArrayB unzipToArrayB(
      NativeApi nativeApi, BlobB blob, Predicate<String> includePredicate)
      throws BytecodeException {
    return unzipBlob(nativeApi.factory(), blob, includePredicate)
        .ifLeft(error -> nativeApi.log().error("Error reading archive: " + error))
        .rightOrGet(() -> null);
  }
}
