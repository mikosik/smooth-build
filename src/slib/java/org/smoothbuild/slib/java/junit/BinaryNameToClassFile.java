package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.compress.UnzipFunc;
import org.smoothbuild.util.collect.DuplicatesDetector;

public class BinaryNameToClassFile {

  public static Map<String, TupleB> binaryNameToClassFile(NativeApi nativeApi,
      Iterable<BlobB> libraryJars) throws IOException, JunitExc {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    Map<String, TupleB> binaryNameToClassFile = new HashMap<>();
    for (BlobB jarBlob : libraryJars) {
      ArrayB fileArray;
      try {
        fileArray = UnzipFunc.unzip(nativeApi, jarBlob, isClassFilePredicate());
      } catch (ZipException e) {
        throw new JunitExc("Cannot read archive. Corrupted data?", e);
      }
      for (TupleB classFile : fileArray.elems(TupleB.class)) {
        String classFilePath = (filePath(classFile)).toJ();
        String binaryName = toBinaryName(classFilePath);
        if (duplicatesDetector.addValue(classFilePath)) {
          throw new JunitExc(
              "File " + classFilePath + " is contained by two different library jar files.");
        } else {
          binaryNameToClassFile.put(binaryName, classFile);
        }
      }
    }
    return binaryNameToClassFile;
  }
}
