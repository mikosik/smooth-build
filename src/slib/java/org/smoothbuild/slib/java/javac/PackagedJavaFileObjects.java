package org.smoothbuild.slib.java.javac;

import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.slib.compress.UnzipFunction.unzip;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipException;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.plugin.NativeApi;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJars(NativeApi nativeApi,
      Iterable<Blob> libraryJars) throws IOException, ZipException {
    Set<InputClassFile> result = new HashSet<>();
    for (Blob jarBlob : libraryJars) {
      Array files = unzip(nativeApi, jarBlob, isClassFilePredicate());
      for (Tuple file : files.asIterable(Tuple.class)) {
        InputClassFile inputClassFile = new InputClassFile(file);
        if (result.contains(inputClassFile)) {
          nativeApi.log().error("File " + filePath(file).jValue()
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
