package org.smoothbuild.lang.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.junit.Test;
import org.smoothbuild.lang.builtin.java.javac.err.IncorrectClassNameGivenByJavaCompilerError;
import org.smoothbuild.message.listen.ErrorMessageException;
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
    manager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, "className", Kind.CLASS, null);
    verifyZeroInteractions(sfm);
  }

  @Test
  public void getJavaFileOutput_logs_error_when_class_name_is_illegal() throws Exception {
    String className = ".illegal.MyClass";

    try {
      manager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, className, Kind.CLASS, null);
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(IncorrectClassNameGivenByJavaCompilerError.class);
    }
    verifyZeroInteractions(sfm);
  }
}
