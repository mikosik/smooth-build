package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;

import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.DuplicatesDetector;

public class BinaryNameToClassFile {

  public static Map<String, SFile> binaryNameToClassFile(NativeApi nativeApi,
      Iterable<Blob> libraryJars) {
    Unjarer unjarer = new Unjarer(nativeApi);
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    Map<String, SFile> binaryNameToClassFile = new HashMap<>();

    for (Blob jarBlob : libraryJars) {
      Array<SFile> fileArray = unjarer.unjar(jarBlob, isClassFilePredicate());

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
