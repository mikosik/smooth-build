package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.lang.message.MessageException.errorException;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJars(Container container,
      Iterable<Blob> libraryJars) {
    Set<InputClassFile> result = new HashSet<>();
    for (Blob jarBlob : libraryJars) {
      Array files = UnzipFunction.unzip(container, (jarBlob), isClassFilePredicate());
      for (SFile file : files.asIterable(SFile.class)) {
        InputClassFile inputClassFile = new InputClassFile(file);
        if (result.contains(inputClassFile)) {
          throw errorException("File " + file.path()
              + " is contained by two different library jar files.");
        } else {
          result.add(inputClassFile);
        }
      }
    }
    return result;
  }
}
