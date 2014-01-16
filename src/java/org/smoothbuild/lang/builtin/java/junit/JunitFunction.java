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
import org.smoothbuild.lang.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.lang.builtin.java.junit.err.JunitTestFailedError;
import org.smoothbuild.lang.builtin.java.junit.err.NoJunitTestFoundWarning;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.PluginApiImpl;

import com.google.common.base.Predicate;

/*
 * TODO
 * Current implementation requires classes from junit.jar being present in
 * smooth.jar. Once plugin system is in place this function should be moved to a
 * plugin and should require junitLib parameter that would provide junit binary.
 */
public class JunitFunction {
  public interface Parameters {
    SArray<SBlob> libs();

    SString include();
  }

  @SmoothFunction(name = "junit")
  public static SString execute(PluginApiImpl pluginApi, Parameters params) {
    return new Worker(pluginApi, params).execute();
  }

  private static class Worker {
    private final PluginApiImpl pluginApi;
    private final Parameters params;

    public Worker(PluginApiImpl pluginApi, Parameters params) {
      this.pluginApi = pluginApi;
      this.params = params;
    }

    public SString execute() {
      Map<String, SFile> binaryNameToClassFile =
          binaryNameToClassFile(pluginApi, nullToEmpty(params.libs()));
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
              pluginApi.log(new JunitTestFailedError(failure));
            }
            return pluginApi.string("FAILURE");
          }
        }
      }
      if (testCount == 0) {
        pluginApi.log(new NoJunitTestFoundWarning());
      }
      return pluginApi.string("SUCCESS");
    }

    private static Class<?> loadClass(FileClassLoader classLoader, String binaryName) {
      try {
        return classLoader.loadClass(binaryName);
      } catch (ClassNotFoundException e) {
        Message errorMessage =
            new Message(ERROR, "Couldn't find class for binaryName = " + binaryName);
        throw new ErrorMessageException(errorMessage);
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
        throw new ErrorMessageException(new IllegalPathPatternError("include", e.getMessage()));
      }
    }
  }
}
