package org.smoothbuild.lang.builtin.java.junit;

import static org.smoothbuild.lang.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.lang.builtin.java.util.JavaNaming.toBinaryName;

import java.util.Map;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.java.Unjarer;
import org.smoothbuild.lang.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.Maps;

public class BinaryNameToClassFile {

  public static Map<String, File> binaryNameToClassFile(Sandbox sandbox, Iterable<File> libraryJars) {
    Unjarer unjarer = new Unjarer(sandbox);
    Map<Path, Path> binaryNameToJar = Maps.newHashMap();
    Map<String, File> binaryNameToClassFile = Maps.newHashMap();

    for (File jarFile : libraryJars) {
      Array<File> fileSet = unjarer.unjarFile(jarFile, isClassFilePredicate());

      for (File classFile : fileSet) {
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
