package org.smoothbuild.slib.java.javac;

import static org.smoothbuild.eval.artifact.FileStruct.filePath;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.eval.artifact.FileStruct;
import org.smoothbuild.plugin.NativeApi;

public class PackagedJavaFileObjects {
  public static Iterable<InputClassFile> classesFromJarFiles(NativeApi nativeApi,
      Iterable<TupleB> libraryJars) throws IOException {
    Set<InputClassFile> result = new HashSet<>();
    for (TupleB jar : libraryJars) {
      BlobB jarBlob = FileStruct.fileContent(jar);
      ArrayB files = nativeApi.unzipper().unzip(jarBlob, isClassFilePredicate());
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
