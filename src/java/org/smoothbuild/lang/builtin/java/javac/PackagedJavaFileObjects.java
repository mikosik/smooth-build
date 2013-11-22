package org.smoothbuild.lang.builtin.java.javac;

import static org.smoothbuild.lang.builtin.java.util.JavaNaming.isClassFilePredicate;

import javax.tools.JavaFileObject;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.java.Unjarer;
import org.smoothbuild.lang.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PackagedJavaFileObjects {
  public static Multimap<String, JavaFileObject> packagedJavaFileObjects(Sandbox sandbox,
      Iterable<SFile> libraryJars) {
    Unjarer unjarer = new Unjarer(sandbox);
    Multimap<String, JavaFileObject> result = HashMultimap.create();

    for (SFile jarFile : libraryJars) {
      SArray<SFile> files = unjarer.unjarFile(jarFile, isClassFilePredicate());
      for (SFile classFile : files) {
        InputClassFile inputClassFile = new InputClassFile(jarFile.path(), classFile);
        String aPackage = inputClassFile.aPackage();
        if (result.containsEntry(aPackage, inputClassFile)) {
          InputClassFile otherInputClassFile = (InputClassFile) result.get(aPackage).iterator()
              .next();
          Path otherJarPath = otherInputClassFile.jarFileName();
          throw new ErrorMessageException(new DuplicateClassFileError(classFile.path(),
              otherJarPath, jarFile.path()));
        } else {
          result.put(aPackage, inputClassFile);
        }
      }
    }

    return result;
  }
}
