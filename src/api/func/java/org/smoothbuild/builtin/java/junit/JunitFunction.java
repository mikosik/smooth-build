package org.smoothbuild.builtin.java.junit;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.builtin.java.util.JavaNaming.isClassFilePredicate;
import static org.smoothbuild.builtin.java.util.JavaNaming.toBinaryName;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.message.MessageException.errorException;

import java.util.Map;
import java.util.function.Predicate;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.WarningMessage;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class JunitFunction {
  @SmoothFunction
  public static SString junit(NativeApi nativeApi, Blob tests, Array deps, SString include) {
    Array unzipped = UnzipFunction.unzip(nativeApi, tests, isClassFilePredicate());
    Map<String, SFile> testFiles = stream(unzipped.asIterable(SFile.class).spliterator(), false)
        .collect(toMap(f -> toBinaryName(f.path().value()), identity()));
    Map<String, SFile> allFiles = binaryNameToClassFile(nativeApi, deps.asIterable(Blob.class));
    testFiles
        .entrySet()
        .stream()
        .forEach(e -> {
          if (allFiles.containsKey(e.getKey())) {
            throw errorException("Both 'tests' and 'deps' contains class " + e.getValue());
          } else {
            allFiles.put(e.getKey(), e.getValue());
          }
        });

    FileClassLoader classLoader = new FileClassLoader(allFiles);
    ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(classLoader);
    try {
      JUnitCoreWrapper jUnitCore = createJUnitCore(allFiles, classLoader);
      Predicate<Path> filter = createFilter(include);
      int testCount = 0;
      for (String binaryName : testFiles.keySet()) {
        Path filePath = path(testFiles.get(binaryName).path().value());
        if (filter.test(filePath)) {
          testCount++;
          Class<?> testClass = loadClass(classLoader, binaryName);
          ResultWrapper result = jUnitCore.run(testClass);
          if (!result.wasSuccessful()) {
            for (FailureWrapper failure : result.getFailures()) {
              nativeApi.log(new ErrorMessage(
                  "test failed: " + failure.toString() + "\n" + failure.getTrace()));
            }
            return nativeApi.create().string("FAILURE");
          }
        }
      }
      if (testCount == 0) {
        nativeApi.log(new WarningMessage("No junit tests found."));
      }
      return nativeApi.create().string("SUCCESS");
    } finally {
      Thread.currentThread().setContextClassLoader(origClassLoader);
    }
  }

  private static JUnitCoreWrapper createJUnitCore(Map<String, SFile> binaryNameToClassFile,
      FileClassLoader classLoader) {
    if (binaryNameToClassFile.containsKey("org.junit.runner.JUnitCore")) {
      return JUnitCoreWrapper.newInstance(loadClass(classLoader, "org.junit.runner.JUnitCore"));
    } else {
      throw errorException("Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
    }
  }

  private static Class<?> loadClass(FileClassLoader classLoader, String binaryName) {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw errorException("Couldn't find class for binaryName = " + binaryName);
    }
  }

  private static Predicate<Path> createFilter(SString includeParam) {
    try {
      return pathMatcher(includeParam.value());
    } catch (IllegalPathPatternException e) {
      throw errorException("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
