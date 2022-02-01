package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.eval.artifact.FileStruct.filePath;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.util.collect.DuplicatesDetector;

import net.lingala.zip4j.exception.ZipException;

public class BinaryNameToClassFile {

  public static Map<String, TupleB> binaryNameToClassFile(NativeApi nativeApi,
      Iterable<BlobB> libraryJars) throws IOException, JunitExc {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    Map<String, TupleB> binaryNameToClassFile = new HashMap<>();
    for (BlobB jarBlob : libraryJars) {
      ArrayB fileArray;
      try {
        fileArray = nativeApi.unzipper().unzip(jarBlob, isClassFilePredicate());
      } catch (ZipException e) {
        throw new JunitExc(
            "Cannot read archive. Corrupted data? Internal message: " + e.getMessage(), e);
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
