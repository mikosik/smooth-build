package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.lang.object.db.FileStruct.filePath;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.slib.compress.UnzipFunction;
import org.smoothbuild.util.DuplicatesDetector;

public class BinaryNameToClassFile {

  public static Map<String, Struct> binaryNameToClassFile(NativeApi nativeApi,
      Iterable<Blob> libraryJars) throws IOException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    Map<String, Struct> binaryNameToClassFile = new HashMap<>();
    for (Blob jarBlob : libraryJars) {
      Array fileArray = UnzipFunction.unzip(nativeApi, jarBlob, isClassFilePredicate());
      for (Struct classFile : fileArray.asIterable(Struct.class)) {
        String classFilePath = (filePath(classFile)).jValue();
        String binaryName = toBinaryName(classFilePath);
        if (duplicatesDetector.addValue(classFilePath)) {
          nativeApi.log().error("File " + classFilePath
              + " is contained by two different library jar files.");
          throw new AbortException();
        } else {
          binaryNameToClassFile.put(binaryName, classFile);
        }
      }
    }
    return binaryNameToClassFile;
  }
}
