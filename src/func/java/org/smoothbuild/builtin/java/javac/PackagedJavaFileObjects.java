package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJars(Container container,
      Iterable<Blob> libraryJars) {
    Set<InputClassFile> result = new HashSet<>();
    for (Blob jarBlob : libraryJars) {
      Array<SFile> files = UnzipFunction.unzip(container, jarBlob, isClassFilePredicate());
      for (SFile classFile : files) {
        InputClassFile inputClassFile = new InputClassFile(classFile);
        if (result.contains(inputClassFile)) {
          throw new ErrorMessage("File " + classFile.path()
              + " is contained by two different library jar files.");
        } else {
          result.add(inputClassFile);
        }
      }
    }
    return result;
  }
}
