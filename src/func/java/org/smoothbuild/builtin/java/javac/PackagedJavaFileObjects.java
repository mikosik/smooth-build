package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.lang.message.MessageType.ERROR;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJars(Container container,
      Iterable<Blob> libraryJars) {
    Unjarer unjarer = new Unjarer(container);
    Set<InputClassFile> result = new HashSet<>();

    for (Blob jarBlob : libraryJars) {
      Array<SFile> files = unjarer.unjar(jarBlob, isClassFilePredicate());
      for (SFile classFile : files) {
        InputClassFile inputClassFile = new InputClassFile(classFile);
        if (result.contains(inputClassFile)) {
          throw new Message(ERROR, "File " + classFile.path()
              + " is contained by two different library jar files.");
        } else {
          result.add(inputClassFile);
        }
      }
    }

    return result;
  }
}
