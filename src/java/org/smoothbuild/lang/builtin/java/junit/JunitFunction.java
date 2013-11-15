package org.smoothbuild.lang.builtin.java.junit;

import static org.smoothbuild.lang.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.util.Empty.nullToEmpty;

import java.util.Map;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.smoothbuild.lang.plugin.File;
import org.smoothbuild.lang.plugin.FileSet;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.plugin.StringValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.SandboxImpl;

/*
 * TODO
 * Current implementation requires classes from junit.jar being present in
 * smooth.jar. Once plugin system is in place this function should be moved to a
 * plugin and should require junitLib parameter that would provide junit binary.
 */
public class JunitFunction {
  public interface Parameters {
    FileSet libs();
  }

  @SmoothFunction(name = "junit")
  public static StringValue execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public StringValue execute() {
      Map<String, File> binaryNameToClassFile = binaryNameToClassFile(sandbox,
          nullToEmpty(params.libs()));
      FileClassLoader classLoader = new FileClassLoader(binaryNameToClassFile);
      JUnitCore jUnitCore = new JUnitCore();

      for (String binaryName : binaryNameToClassFile.keySet()) {
        if (binaryName.endsWith("Test")) {
          Class<?> testClass = loadClass(classLoader, binaryName);
          Result result = jUnitCore.run(testClass);
          if (!result.wasSuccessful()) {
            for (Failure failure : result.getFailures()) {
              sandbox.report(new JunitTestFailedError(failure));
            }
            return sandbox.string("FAILURE");
          }
        }
      }
      return sandbox.string("SUCCESS");
    }

    private static Class<?> loadClass(FileClassLoader classLoader, String binaryName) {
      try {
        return classLoader.loadClass(binaryName);
      } catch (ClassNotFoundException e) {
        Message errorMessage = new Message(ERROR, "Couldn't find class for binaryName = "
            + binaryName);
        throw new ErrorMessageException(errorMessage);
      }
    }
  }
}
