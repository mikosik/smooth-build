package org.smoothbuild.slib.java.javac;

import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.slib.compress.UnzipFunc.unzip;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipException;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.exec.base.FileStruct;
import org.smoothbuild.plugin.NativeApi;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJarFiles(NativeApi nativeApi,
      Iterable<TupleB> libraryJars) throws IOException, ZipException {
    Set<InputClassFile> result = new HashSet<>();
    for (TupleB jar : libraryJars) {
      BlobB jarBlob = FileStruct.fileContent(jar);
      ArrayB files = unzip(nativeApi, jarBlob, isClassFilePredicate());
      for (TupleB file : files.elems(TupleB.class)) {
        InputClassFile inputClassFile = new InputClassFile(file);
        if (result.contains(inputClassFile)) {
          nativeApi.log().error("File " + filePath(file).toJ()
              + " is contained by two different library jar files.");
          return null;
        } else {
          result.add(inputClassFile);
        }
      }
    }
    return result;
  }
}
