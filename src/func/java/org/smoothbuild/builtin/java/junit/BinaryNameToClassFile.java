package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;

import java.util.Map;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.Maps;

public class BinaryNameToClassFile {

  public static Map<String, SFile> binaryNameToClassFile(NativeApi nativeApi,
      Iterable<Blob> libraryJars) {
    Unjarer unjarer = new Unjarer(nativeApi);
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    Map<String, SFile> binaryNameToClassFile = Maps.newHashMap();

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
