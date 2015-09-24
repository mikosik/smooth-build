package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Map;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.builtin.java.junit.err.JunitTestFailedError;
import org.smoothbuild.builtin.java.junit.err.NoJunitTestFoundWarning;
import org.smoothbuild.builtin.util.Predicate;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.message.base.Message;

public class JunitFunction {
  @SmoothFunction
  public static SString junit( //
      Container container, //
      @Name("libs") Array<Blob> libs, //
      @Name("include") SString include) {
    Map<String, SFile> binaryNameToClassFile = binaryNameToClassFile(container, libs);
    FileClassLoader classLoader = new FileClassLoader(binaryNameToClassFile);
    JUnitCore jUnitCore = new JUnitCore();

    Predicate<Path> filter = createFilter(include);
    int testCount = 0;
    for (String binaryName : binaryNameToClassFile.keySet()) {
      Path filePath = binaryNameToClassFile.get(binaryName).path();
      if (filter.test(filePath)) {
        testCount++;
        Class<?> testClass = loadClass(classLoader, binaryName);
        Result result = jUnitCore.run(testClass);
        if (!result.wasSuccessful()) {
          for (Failure failure : result.getFailures()) {
            container.log(new JunitTestFailedError(failure));
          }
          return container.create().string("FAILURE");
        }
      }
    }
    if (testCount == 0) {
      container.log(new NoJunitTestFoundWarning());
    }
    return container.create().string("SUCCESS");
  }

  private static Class<?> loadClass(FileClassLoader classLoader, String binaryName) {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new Message(ERROR, "Couldn't find class for binaryName = " + binaryName);
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
      throw new IllegalPathPatternError("include", e.getMessage());
    }
  }
}
