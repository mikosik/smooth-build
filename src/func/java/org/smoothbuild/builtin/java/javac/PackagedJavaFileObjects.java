package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;

import javax.tools.JavaFileObject;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SValueFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PackagedJavaFileObjects {
  public static Multimap<String, JavaFileObject> packagedJavaFileObjects(
      SValueFactory valueFactory, Iterable<Blob> libraryJars) {
    Unjarer unjarer = new Unjarer(valueFactory);
    Multimap<String, JavaFileObject> result = HashMultimap.create();

    for (Blob jarBlob : libraryJars) {
      SArray<SFile> files = unjarer.unjar(jarBlob, isClassFilePredicate());
      for (SFile classFile : files) {
        InputClassFile inputClassFile = new InputClassFile(classFile);
        String aPackage = inputClassFile.aPackage();
        if (result.containsEntry(aPackage, inputClassFile)) {
          throw new DuplicateClassFileError(classFile.path());
        } else {
          result.put(aPackage, inputClassFile);
        }
      }
    }

    return result;
  }
}
