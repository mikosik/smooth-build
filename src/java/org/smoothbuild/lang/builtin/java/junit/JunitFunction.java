package org.smoothbuild.lang.builtin.java.junit;

import static org.smoothbuild.io.fs.match.PathMatcher.pathMatcher;
import static org.smoothbuild.lang.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.util.Empty.nullToEmpty;

import java.util.Map;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.match.IllegalPathPatternException;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.lang.builtin.java.junit.err.JunitTestFailedError;
import org.smoothbuild.lang.builtin.java.junit.err.NoJunitTestFoundWarning;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.base.Predicate;

public class JunitFunction {
  public static SString execute(NativeApiImpl nativeApi, BuiltinSmoothModule.JunitParameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApiImpl nativeApi;
    private final BuiltinSmoothModule.JunitParameters params;

    public Worker(NativeApiImpl nativeApi, BuiltinSmoothModule.JunitParameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
    }

    public SString execute() {
      Map<String, SFile> binaryNameToClassFile =
          binaryNameToClassFile(nativeApi, nullToEmpty(params.libs()));
      FileClassLoader classLoader = new FileClassLoader(binaryNameToClassFile);
      JUnitCore jUnitCore = new JUnitCore();

      Predicate<Path> filter = createFilter();
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

    private Predicate<Path> createFilter() {
      SString includeParam = params.include();
      if (includeParam == null) {
        return createFilter("**/*Test.class");
      } else {
        return createFilter(includeParam.value());
      }
    }

    private Predicate<Path> createFilter(String includeExpression) {
      try {
        return pathMatcher(includeExpression);
      } catch (IllegalPathPatternException e) {
        throw new IllegalPathPatternError("include", e.getMessage());
      }
    }
  }
}
