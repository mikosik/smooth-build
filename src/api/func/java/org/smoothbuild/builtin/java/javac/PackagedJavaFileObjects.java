package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.lang.message.MessageException.errorException;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.SString;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJars(NativeApi nativeApi,
      Iterable<Blob> libraryJars) {
    Set<InputClassFile> result = new HashSet<>();
    for (Blob jarBlob : libraryJars) {
      Array files = UnzipFunction.unzip(nativeApi, (jarBlob), isClassFilePredicate());
      for (Struct file : files.asIterable(Struct.class)) {
        InputClassFile inputClassFile = new InputClassFile(file);
        if (result.contains(inputClassFile)) {
          throw errorException("File " + (SString) file.get("path")
              + " is contained by two different library jar files.");
        } else {
          result.add(inputClassFile);
        }
      }
    }
    return result;
  }
}
