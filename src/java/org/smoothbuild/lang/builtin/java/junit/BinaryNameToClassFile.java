package org.smoothbuild.lang.builtin.java.junit;

import static org.smoothbuild.lang.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.lang.builtin.java.util.JavaNaming.toBinaryName;

import java.util.Map;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.java.Unjarer;
import org.smoothbuild.lang.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.Maps;

public class BinaryNameToClassFile {

  public static Map<String, SFile> binaryNameToClassFile(NativeApi nativeApi,
      Iterable<SBlob> libraryJars) {
    Unjarer unjarer = new Unjarer(nativeApi);
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<Path>();
    Map<String, SFile> binaryNameToClassFile = Maps.newHashMap();

    for (SBlob jarBlob : libraryJars) {
      SArray<SFile> fileArray = unjarer.unjar(jarBlob, isClassFilePredicate());

      for (SFile classFile : fileArray) {
        Path classFilePath = classFile.path();
        String binaryName = toBinaryName(classFilePath);
        if (duplicatesDetector.addValue(classFilePath)) {
          throw new DuplicateClassFileError(classFilePath);
        } else {
          binaryNameToClassFile.put(binaryName, classFile);
        }
      }
    }
    return binaryNameToClassFile;
  }
}
