package org.smoothbuild.lang.builtin.java.javac;

import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.junit.Test;
import org.smoothbuild.lang.builtin.java.javac.err.IncorrectClassNameGivenByJavaCompilerError;
import org.smoothbuild.testing.task.exec.FakePluginApi;

import com.google.common.collect.Multimap;

public class SandboxedJavaFileManagerTest {
  StandardJavaFileManager sfm = mock(StandardJavaFileManager.class);
  FakePluginApi pluginApi = new FakePluginApi();
  @SuppressWarnings("unchecked")
  Multimap<String, JavaFileObject> packagedJavaFileObjects = mock(Multimap.class);

  SandboxedJavaFileManager manager = new SandboxedJavaFileManager(sfm, pluginApi,
      packagedJavaFileObjects);

  @Test
  public void getJavaFile_output_is_not_forwarded_to_standard_manager_for_class_output()
      throws Exception {
    when(manager)
        .getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, "className", Kind.CLASS, null);
    thenCalledTimes(0, onInstance(sfm));
  }

  @Test
  public void getJavaFileOutput_logs_error_when_class_name_is_illegal() throws Exception {
    String className = ".illegal.MyClass";

    try {
      manager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, className, Kind.CLASS, null);
      fail("exception should be thrown");
    } catch (IncorrectClassNameGivenByJavaCompilerError e) {
      // expected
    }
    thenCalledTimes(0, onInstance(sfm));
  }
}
