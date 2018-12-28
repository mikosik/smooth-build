package org.smoothbuild.builtin.java.junit;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class JunitFunction {
  @SmoothFunction("junit")
  public static SString junit(NativeApi nativeApi, Blob tests, Array deps, SString include)
      throws IOException {
    Array unzipped = UnzipFunction.unzip(nativeApi, tests, isClassFilePredicate());
    Map<String, Struct> testFiles = stream(unzipped.asIterable(Struct.class).spliterator(), false)
        .collect(toMap(f -> toBinaryName(((SString) f.get("path")).data()), identity()));
    Map<String, Struct> allFiles = binaryNameToClassFile(nativeApi, deps.asIterable(Blob.class));
    for (Entry<String, Struct> entry : testFiles.entrySet()) {
      if (allFiles.containsKey(entry.getKey())) {
        nativeApi.log().error("Both 'tests' and 'deps' contains class " + entry.getValue());
        return null;
      } else {
        allFiles.put(entry.getKey(), entry.getValue());
      }
    }

    FileClassLoader classLoader = new FileClassLoader(allFiles);
    ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(classLoader);
    try {
      JUnitCoreWrapper jUnitCore = createJUnitCore(nativeApi, allFiles, classLoader);
      Predicate<Path> filter = createFilter(nativeApi, include);
      int testCount = 0;
      for (String binaryName : testFiles.keySet()) {
        Path filePath = path(((SString) testFiles.get(binaryName).get("path")).data());
        if (filter.test(filePath)) {
          testCount++;
          Class<?> testClass = loadClass(nativeApi, classLoader, binaryName);
          ResultWrapper result = jUnitCore.run(testClass);
          if (!result.wasSuccessful()) {
            for (FailureWrapper failure : result.getFailures()) {
              nativeApi.log().error(
                  "test failed: " + failure.toString() + "\n" + failure.getTrace());
            }
            return nativeApi.create().string("FAILURE");
          }
        }
      }
      if (testCount == 0) {
        nativeApi.log().warning("No junit tests found.");
      }
      return nativeApi.create().string("SUCCESS");
    } finally {
      Thread.currentThread().setContextClassLoader(origClassLoader);
    }
  }

  private static JUnitCoreWrapper createJUnitCore(NativeApi nativeApi,
      Map<String, Struct> binaryNameToClassFile, FileClassLoader classLoader) {
    if (binaryNameToClassFile.containsKey("org.junit.runner.JUnitCore")) {
      return JUnitCoreWrapper.newInstance(nativeApi,
          loadClass(nativeApi, classLoader, "org.junit.runner.JUnitCore"));
    } else {
      nativeApi.log().error(
          "Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
      throw new AbortException();
    }
  }

  private static Class<?> loadClass(NativeApi nativeApi, FileClassLoader classLoader,
      String binaryName) {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      nativeApi.log().error("Couldn't find class for binaryName = " + binaryName);
      throw new AbortException();
    }
  }

  private static Predicate<Path> createFilter(NativeApi nativeApi, SString includeParam) {
    try {
      return pathMatcher(includeParam.data());
    } catch (IllegalPathPatternException e) {
      nativeApi.log().error("Parameter 'include' has illegal value. " + e.getMessage());
      throw new AbortException();
    }
  }
}
