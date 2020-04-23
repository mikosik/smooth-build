package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJars(NativeApi nativeApi,
      Iterable<Blob> libraryJars) throws IOException {
    Set<InputClassFile> result = new HashSet<>();
    for (Blob jarBlob : libraryJars) {
      Array files = UnzipFunction.unzip(nativeApi, jarBlob, isClassFilePredicate());
      for (Struct file : files.asIterable(Struct.class)) {
        InputClassFile inputClassFile = new InputClassFile(file);
        if (result.contains(inputClassFile)) {
          nativeApi.log().error("File " + file.get("path")
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
