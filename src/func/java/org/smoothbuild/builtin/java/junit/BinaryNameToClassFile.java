package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;

import java.util.Map;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SValueFactory;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.collect.Maps;

public class BinaryNameToClassFile {

  public static Map<String, SFile> binaryNameToClassFile(SValueFactory valueFactory,
      Iterable<SBlob> libraryJars) {
    Unjarer unjarer = new Unjarer(valueFactory);
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
