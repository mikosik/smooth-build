package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.lang.message.MessageType.ERROR;

import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.builtin.java.Unjarer;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.util.DuplicatesDetector;

public class BinaryNameToClassFile {

  public static Map<String, SFile> binaryNameToClassFile(Container container,
      Iterable<Blob> libraryJars) {
    Unjarer unjarer = new Unjarer(container);
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    Map<String, SFile> binaryNameToClassFile = new HashMap<>();

    for (Blob jarBlob : libraryJars) {
      Array<SFile> fileArray = unjarer.unjar(jarBlob, isClassFilePredicate());

      for (SFile classFile : fileArray) {
        String classFilePath = classFile.path().value();
        String binaryName = toBinaryName(classFilePath);
        if (duplicatesDetector.addValue(classFilePath)) {
          throw new Message(ERROR, "File " + classFilePath
              + " is contained by two different library jar files.");
        } else {
          binaryNameToClassFile.put(binaryName, classFile);
        }
      }
    }
    return binaryNameToClassFile;
  }
}
