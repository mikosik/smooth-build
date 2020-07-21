package org.smoothbuild.slib.java.javac;

import static org.smoothbuild.record.db.FileStruct.filePath;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.plugin.AbortException;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.slib.compress.UnzipFunction;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJars(NativeApi nativeApi,
      Iterable<Blob> libraryJars) throws IOException {
    Set<InputClassFile> result = new HashSet<>();
    for (Blob jarBlob : libraryJars) {
      Array files = UnzipFunction.unzip(nativeApi, jarBlob, isClassFilePredicate());
      for (Tuple file : files.asIterable(Tuple.class)) {
        InputClassFile inputClassFile = new InputClassFile(file);
        if (result.contains(inputClassFile)) {
          nativeApi.log().error("File " + filePath(file)
              + " is contained by two different library jar files.");
          throw new AbortException();
        } else {
          result.add(inputClassFile);
        }
      }
    }
    return result;
  }
}
