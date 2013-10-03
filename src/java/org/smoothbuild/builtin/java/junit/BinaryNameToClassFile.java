package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;

import java.util.Map;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.mem.MemoryFileSystem;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.impl.MutableStoredFileSet;

import com.google.common.collect.Maps;

public class BinaryNameToClassFile {

  public static Map<String, File> binaryNameToClassFile(Iterable<File> libraryJars) {
    Unjarer unjarer = new Unjarer();
    Map<Path, Path> binaryNameToJar = Maps.newHashMap();
    Map<String, File> binaryNameToClassFile = Maps.newHashMap();

    for (File jarFile : libraryJars) {
      MutableStoredFileSet files = new MutableStoredFileSet(new MemoryFileSystem());
      unjarer.unjarFile(jarFile, isClassFilePredicate(), files);

      for (File classFile : files) {
        Path path = classFile.path();
        String binaryName = toBinaryName(path);
        if (binaryNameToJar.containsKey(binaryName)) {
          Path otherJarPath = binaryNameToJar.get(binaryName);
          throw new ErrorMessageException(new DuplicateClassFileError(path, otherJarPath,
              jarFile.path()));
        } else {
          binaryNameToClassFile.put(binaryName, classFile);
          binaryNameToJar.put(path, jarFile.path());
        }
      }
    }
    return binaryNameToClassFile;
  }

}
