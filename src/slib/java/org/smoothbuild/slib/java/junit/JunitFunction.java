package org.smoothbuild.slib.java.junit;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.compress.UnzipFunction.unzip;
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

import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.exec.base.FileStruct;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.slib.file.match.IllegalPathPatternException;

import com.google.common.collect.Streams;

public class JunitFunction {
  public static Str function(NativeApi nativeApi, Tuple tests, Array deps, Str include)
      throws IOException {

    try {
      Array unzipped = unzipTestFiles(nativeApi, tests);
      Map<String, Tuple> testFiles = stream(unzipped.elements(Tuple.class).spliterator(), false)
          .collect(toMap(f -> toBinaryName(filePath(f).jValue()), identity()));
      Map<String, Tuple> allFiles = buildNameFileMap(nativeApi, deps, testFiles);

      FileClassLoader classLoader = new FileClassLoader(allFiles);
      ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(classLoader);
      try {
        JUnitCoreWrapper jUnitCore = createJUnitCore(nativeApi, allFiles, classLoader);
        Predicate<Path> filter = createFilter(include);
        int testCount = 0;
        for (String binaryName : testFiles.keySet()) {
          Path filePath = path(filePath(testFiles.get(binaryName)).jValue());
          if (filter.test(filePath)) {
            testCount++;
            Class<?> testClass = loadClass(classLoader, binaryName);
            ResultWrapper result = jUnitCore.run(testClass);
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
    } catch (JunitException e) {
      nativeApi.log().error(e.getMessage());
      return null;
    }
  }

  private static Map<String, Tuple> buildNameFileMap(NativeApi nativeApi, Array deps,
      Map<String, Tuple> testFiles) throws IOException, JunitException {
    Iterable<Blob> libraryJars = Streams.stream(deps.elements(Tuple.class))
        .map(FileStruct::fileContent)
        .collect(toList());
    Map<String, Tuple> allFiles = binaryNameToClassFile(nativeApi, libraryJars);
    for (Entry<String, Tuple> entry : testFiles.entrySet()) {
      if (allFiles.containsKey(entry.getKey())) {
        throw new JunitException("Both 'tests' and 'deps' contains class " + entry.getValue());
      } else {
        allFiles.put(entry.getKey(), entry.getValue());
      }
    }
    return allFiles;
  }

  private static Array unzipTestFiles(NativeApi nativeApi, Tuple tests) throws IOException,
      JunitException {
    try {
      return unzip(nativeApi, fileContent(tests), isClassFilePredicate());
    } catch (ZipException e) {
      throw new JunitException("Cannot read archive from 'tests' param. Corrupted data?");
    }
  }

  private static JUnitCoreWrapper createJUnitCore(NativeApi nativeApi,
      Map<String, Tuple> binaryNameToClassFile, FileClassLoader classLoader) throws
      JunitException {
    if (binaryNameToClassFile.containsKey("org.junit.runner.JUnitCore")) {
      return newInstance(
          nativeApi, loadClass(classLoader, "org.junit.runner.JUnitCore"));
    } else {
      throw new JunitException(
          "Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
    }
  }

  private static Class<?> loadClass(FileClassLoader classLoader, String binaryName)
      throws JunitException {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new JunitException("Couldn't find class for binaryName = " + binaryName);
    }
  }

  private static Predicate<Path> createFilter(Str includeParam) throws JunitException {
    try {
      return pathMatcher(includeParam.jValue());
    } catch (IllegalPathPatternException e) {
      throw new JunitException("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
