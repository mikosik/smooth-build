package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJars(NativeApi nativeApi,
      Iterable<Blob> libraryJars) {
    Unjarer unjarer = new Unjarer(nativeApi);
    Set<InputClassFile> result = new HashSet<>();

    for (Blob jarBlob : libraryJars) {
      Array<SFile> files = unjarer.unjar(jarBlob, isClassFilePredicate());
      for (SFile classFile : files) {
        InputClassFile inputClassFile = new InputClassFile(classFile);
        if (result.contains(inputClassFile)) {
          throw new DuplicateClassFileError(classFile.path());
        } else {
          result.add(inputClassFile);
        }
      }
    }

    return result;
  }
}
