package org.smoothbuild.builtin.java.javac;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;

import javax.tools.JavaFileObject;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.MutableStoredFileSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class PackagedJavaFileObjects {
  public static Multimap<String, JavaFileObject> packagedJavaFileObjects(Iterable<File> libraryJars) {
    Unjarer unjarer = new Unjarer();
    Multimap<String, JavaFileObject> result = HashMultimap.create();

    for (File jarFile : libraryJars) {
      MutableStoredFileSet files = new MutableStoredFileSet(new MemoryFileSystem());
      unjarer.unjarFile(jarFile, isClassFilePredicate(), files);
      for (File classFile : files) {
        InputClassFile inputClassFile = new InputClassFile(jarFile.path(), classFile);
        String aPackage = inputClassFile.aPackage();
        if (result.containsEntry(aPackage, inputClassFile)) {
          InputClassFile otherInputClassFile = (InputClassFile) result.get(aPackage).iterator()
              .next();
          Path otherJarPath = otherInputClassFile.jarFileName();
          throw new DuplicateClassFileError(classFile.path(), otherJarPath, jarFile.path());
        } else {
          result.put(aPackage, inputClassFile);
        }
      }
    }

    return result;
  }
}
