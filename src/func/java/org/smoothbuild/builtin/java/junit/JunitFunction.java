package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.util.Empty.nullToEmpty;

import java.util.Map;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.builtin.file.match.IllegalPathPatternException;
import org.smoothbuild.builtin.java.junit.err.JunitTestFailedError;
import org.smoothbuild.builtin.java.junit.err.NoJunitTestFoundWarning;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.message.base.Message;

import com.google.common.base.Predicate;

public class JunitFunction {

  public interface JunitParameters {
    Array<Blob> libs();

    SString include();
  }

  @SmoothFunction
  public static SString junit(NativeApi nativeApi, JunitParameters params) {
    Map<String, SFile> binaryNameToClassFile = binaryNameToClassFile(nativeApi, nullToEmpty(
        params.libs()));
    FileClassLoader classLoader = new FileClassLoader(binaryNameToClassFile);
    JUnitCore jUnitCore = new JUnitCore();

    Predicate<Path> filter = createFilter(params.include());
    int testCount = 0;
    for (String binaryName : binaryNameToClassFile.keySet()) {
      Path filePath = binaryNameToClassFile.get(binaryName).path();
      if (filter.apply(filePath)) {
        testCount++;
        Class<?> testClass = loadClass(classLoader, binaryName);
        Result result = jUnitCore.run(testClass);
        if (!result.wasSuccessful()) {
          for (Failure failure : result.getFailures()) {
            nativeApi.log(new JunitTestFailedError(failure));
          }
          return nativeApi.string("FAILURE");
        }
      }
    }
    if (testCount == 0) {
      nativeApi.log(new NoJunitTestFoundWarning());
    }
    return nativeApi.string("SUCCESS");
  }

  private static Class<?> loadClass(FileClassLoader classLoader, String binaryName) {
    try {
      return classLoader.loadClass(binaryName);
    } catch (ClassNotFoundException e) {
      throw new Message(ERROR, "Couldn't find class for binaryName = " + binaryName);
    }
  }

  private static Predicate<Path> createFilter(SString includeParam) {
    if (includeParam == null) {
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
