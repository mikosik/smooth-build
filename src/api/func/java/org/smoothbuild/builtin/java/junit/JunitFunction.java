package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.message.MessageException.errorException;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
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
  public static SString junit(NativeApi nativeApi, Array libs, SString include) {
    ArrayList<Blob> libBlobs = new ArrayList<>();
    for (Blob lib : libs.asIterable(Blob.class)) {
      libBlobs.add(lib);
    }
    Map<String, SFile> binaryNameToClassFile = binaryNameToClassFile(nativeApi, libBlobs);
    FileClassLoader classLoader = new FileClassLoader(binaryNameToClassFile);
    ClassLoader origClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(classLoader);
    try {
      JUnitCore jUnitCore = new JUnitCore();
      Predicate<Path> filter = createFilter(include);
      int testCount = 0;
      for (String binaryName : binaryNameToClassFile.keySet()) {
        Path filePath = path(binaryNameToClassFile.get(binaryName).path().value());
        if (filter.test(filePath)) {
          testCount++;
          Class<?> testClass = loadClass(classLoader, binaryName);
          Result result = jUnitCore.run(testClass);
          if (!result.wasSuccessful()) {
            for (Failure failure : result.getFailures()) {
              nativeApi.log(new ErrorMessage("test failed: " + failure.toString() + "\n" + failure
                  .getTrace()));
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

  private static Class<?> loadClass(FileClassLoader classLoader, String binaryName) {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw errorException("Couldn't find class for binaryName = " + binaryName);
    }
  }

  private static Predicate<Path> createFilter(SString includeParam) {
    if (includeParam == null || includeParam.value().isEmpty()) {
      return createFilter("**/*Test.class");
    } else {
      return createFilter(includeParam.value());
    }
  }

  private static Predicate<Path> createFilter(String includeExpression) {
    try {
      return pathMatcher(includeExpression);
    } catch (IllegalPathPatternException e) {
      throw errorException("Parameter 'include' has illegal value. " + e.getMessage());
    }
  }
}
