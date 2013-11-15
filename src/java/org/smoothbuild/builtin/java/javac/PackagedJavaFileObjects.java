package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;

import javax.tools.JavaFileObject;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Sandbox;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PackagedJavaFileObjects {
  public static Multimap<String, JavaFileObject> packagedJavaFileObjects(Sandbox sandbox,
      Iterable<File> libraryJars) {
    Unjarer unjarer = new Unjarer(sandbox);
    Multimap<String, JavaFileObject> result = HashMultimap.create();

    for (File jarFile : libraryJars) {
      FileSet files = unjarer.unjarFile(jarFile, isClassFilePredicate());
      for (File classFile : files) {
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
