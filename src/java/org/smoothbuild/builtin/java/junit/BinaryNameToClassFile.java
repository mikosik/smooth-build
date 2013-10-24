package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;

import java.util.Map;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.Sandbox;

import com.google.common.collect.Maps;

public class BinaryNameToClassFile {

  public static Map<String, File> binaryNameToClassFile(Sandbox sandbox, Iterable<File> libraryJars) {
    Unjarer unjarer = new Unjarer(sandbox);
    Map<Path, Path> binaryNameToJar = Maps.newHashMap();
    Map<String, File> binaryNameToClassFile = Maps.newHashMap();

    for (File jarFile : libraryJars) {
      FileSet fileSet = unjarer.unjarFile(jarFile, isClassFilePredicate());

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
