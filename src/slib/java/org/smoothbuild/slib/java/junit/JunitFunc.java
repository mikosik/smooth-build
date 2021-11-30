package org.smoothbuild.slib.java.junit;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.compress.UnzipFunc.unzip;
import static org.smoothbuild.slib.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.slib.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.slib.java.junit.JUnitCoreWrapper.newInstance;
import static org.smoothbuild.slib.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.slib.java.util.JavaNaming.toBinaryName;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.zip.ZipException;

import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.exec.base.FileStruct;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.file.match.IllegalPathPatternExc;

public class JunitFunc {
  public static StringH func(NativeApi nativeApi, TupleH tests, ArrayH deps, StringH include)
      throws IOException {

    try {
      ArrayH unzipped = unzipTestFiles(nativeApi, tests);
      Map<String, TupleH> testFiles = stream(unzipped.elems(TupleH.class).spliterator(), false)
          .collect(toMap(f -> toBinaryName(filePath(f).toJ()), identity()));
      Map<String, TupleH> allFiles = buildNameFileMap(nativeApi, deps, testFiles);

      FileClassLoader classLoader = new FileClassLoader(allFiles);
      ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(classLoader);
      try {
        JUnitCoreWrapper jUnitCore = createJUnitCore(nativeApi, allFiles, classLoader);
        Predicate<Path> filter = createFilter(include);
        int testCount = 0;
        for (String binaryName : testFiles.keySet()) {
          Path filePath = path(filePath(testFiles.get(binaryName)).toJ());
          if (filter.test(filePath)) {
            testCount++;
            Class<?> testClass = loadClass(classLoader, binaryName);
            ResWrapper result = jUnitCore.run(testClass);
            if (!result.wasSuccessful()) {
              for (FailureWrapper failure : result.getFailures()) {
                nativeApi.log().error(
                    "test failed: " + failure.toString() + "\n" + failure.getTrace());
              }
              return nativeApi.factory().string("FAILURE");
            }
          }
        }
        if (testCount == 0) {
          nativeApi.log().warning("No junit tests found.");
        }
        return nativeApi.factory().string("SUCCESS");
      } finally {
        Thread.currentThread().setContextClassLoader(origClassLoader);
      }
    } catch (JunitExc e) {
      nativeApi.log().error(e.getMessage());
      return null;
    }
  }

  private static Map<String, TupleH> buildNameFileMap(NativeApi nativeApi, ArrayH deps,
      Map<String, TupleH> testFiles) throws IOException, JunitExc {
    Iterable<BlobH> libraryJars = deps.elems(TupleH.class)
        .stream()
        .map(FileStruct::fileContent)
        .collect(toList());
    Map<String, TupleH> allFiles = binaryNameToClassFile(nativeApi, libraryJars);
    for (Entry<String, TupleH> entry : testFiles.entrySet()) {
      if (allFiles.containsKey(entry.getKey())) {
        throw new JunitExc("Both 'tests' and 'deps' contains class " + entry.getValue());
      } else {
        allFiles.put(entry.getKey(), entry.getValue());
      }
    }
    return allFiles;
  }

  private static ArrayH unzipTestFiles(NativeApi nativeApi, TupleH tests) throws IOException,
      JunitExc {
    try {
      return unzip(nativeApi, fileContent(tests), isClassFilePredicate());
    } catch (ZipException e) {
      throw new JunitExc("Cannot read archive from 'tests' param. Corrupted data?");
    }
  }

  private static JUnitCoreWrapper createJUnitCore(NativeApi nativeApi,
      Map<String, TupleH> binaryNameToClassFile, FileClassLoader classLoader)
      throws JunitExc {
    if (binaryNameToClassFile.containsKey("org.junit.runner.JUnitCore")) {
      return newInstance(
          nativeApi, loadClass(classLoader, "org.junit.runner.JUnitCore"));
    } else {
      throw new JunitExc(
          "Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
    }
  }

  private static Class<?> loadClass(FileClassLoader classLoader, String binaryName)
      throws JunitExc {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new JunitExc("Couldn't find class for binaryName = " + binaryName);
    }
  }

  private static Predicate<Path> createFilter(StringH includeParam) throws JunitExc {
    try {
      return pathMatcher(includeParam.toJ());
    } catch (IllegalPathPatternExc e) {
      throw new JunitExc("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
