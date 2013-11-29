package org.smoothbuild.lang.builtin.java.junit;

import static org.smoothbuild.lang.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.lang.builtin.java.util.JavaNaming.toBinaryName;

import java.util.Map;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.java.Unjarer;
import org.smoothbuild.lang.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.Maps;

public class BinaryNameToClassFile {

  public static Map<String, SFile> binaryNameToClassFile(PluginApi pluginApi,
      Iterable<SBlob> libraryJars) {
    Unjarer unjarer = new Unjarer(pluginApi);
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<Path>();
    Map<String, SFile> binaryNameToClassFile = Maps.newHashMap();

    for (SBlob jarFile : libraryJars) {
      SArray<SFile> fileArray = unjarer.unjarFile(jarFile, isClassFilePredicate());

      for (SFile classFile : fileArray) {
        Path classFilePath = classFile.path();
        String binaryName = toBinaryName(classFilePath);
        if (duplicatesDetector.add(classFilePath)) {
          throw new ErrorMessageException(new DuplicateClassFileError(classFilePath));
        } else {
          binaryNameToClassFile.put(binaryName, classFile);
        }
      }
    }
    return binaryNameToClassFile;
  }
}
