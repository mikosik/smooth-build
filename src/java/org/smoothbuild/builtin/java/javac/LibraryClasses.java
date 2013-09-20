package org.smoothbuild.builtin.java.javac;

import java.io.IOException;

import javax.tools.JavaFileObject;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicatedClassFileError;
import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.PluginErrorException;
import org.smoothbuild.plugin.internal.MutableStoredFileSet;
import org.smoothbuild.util.EndsWithPredicate;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class LibraryClasses {
  private static Predicate<String> IS_CLASS_FILE = new EndsWithPredicate(".class");

  private final Multimap<String, JavaFileObject> packageToClassesMap;

  public static LibraryClasses libraryClasses(Iterable<File> libraryJars) throws IOException {
    Unjarer unjarer = new Unjarer();
    Multimap<String, JavaFileObject> map = HashMultimap.create();

    for (File jarFile : libraryJars) {
      String jarFileName = jarFile.path().value();
      MutableStoredFileSet files = new MutableStoredFileSet(new MemoryFileSystem());
      unjarer.unjarFile(jarFile, IS_CLASS_FILE, files);
      for (File classFile : files) {
        InputClassFile inputClassFile = new InputClassFile(jarFileName, classFile);
        String aPackage = inputClassFile.aPackage();
        if (map.containsEntry(aPackage, inputClassFile)) {
          InputClassFile otherInputClassFile = (InputClassFile) map.get(aPackage).iterator().next();
          String otherJarFileName = otherInputClassFile.jarFileName();
          throw new PluginErrorException(new DuplicatedClassFileError(classFile.path(),
              otherJarFileName, jarFileName));
        } else {
          map.put(aPackage, inputClassFile);
        }
      }
    }

    return new LibraryClasses(map);
  }

  private LibraryClasses(Multimap<String, JavaFileObject> packageToClassesMap) {
    this.packageToClassesMap = packageToClassesMap;
  }

  public Iterable<JavaFileObject> classesInPackage(String packageName) {
    return packageToClassesMap.get(packageName);
  }
}
