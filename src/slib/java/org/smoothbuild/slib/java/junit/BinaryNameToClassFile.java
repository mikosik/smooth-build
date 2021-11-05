package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.compress.UnzipFunction;
import org.smoothbuild.util.collect.DuplicatesDetector;

public class BinaryNameToClassFile {

  public static Map<String, Tuple> binaryNameToClassFile(NativeApi nativeApi,
      Iterable<Blob> libraryJars) throws IOException, JunitException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    Map<String, Tuple> binaryNameToClassFile = new HashMap<>();
    for (Blob jarBlob : libraryJars) {
      Array fileArray;
      try {
        fileArray = UnzipFunction.unzip(nativeApi, jarBlob, isClassFilePredicate());
      } catch (ZipException e) {
        throw new JunitException("Cannot read archive. Corrupted data?", e);
      }
      for (Tuple classFile : fileArray.elements(Tuple.class)) {
        String classFilePath = (filePath(classFile)).jValue();
        String binaryName = toBinaryName(classFilePath);
        if (duplicatesDetector.addValue(classFilePath)) {
          throw new JunitException(
              "File " + classFilePath + " is contained by two different library jar files.");
        } else {
          binaryNameToClassFile.put(binaryName, classFile);
        }
      }
    }
    return binaryNameToClassFile;
  }
}
