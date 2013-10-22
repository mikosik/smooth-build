package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.junit.Test;
import org.smoothbuild.builtin.java.javac.err.IncorrectClassNameGivenByJavaCompilerError;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.Multimap;

public class SandboxedJavaFileManagerTest {
  StandardJavaFileManager sfm = mock(StandardJavaFileManager.class);
  FakeSandbox sandbox = new FakeSandbox();
  @SuppressWarnings("unchecked")
  Multimap<String, JavaFileObject> packagedJavaFileObjects = mock(Multimap.class);

  SandboxedJavaFileManager manager = new SandboxedJavaFileManager(sfm, sandbox,
      packagedJavaFileObjects);

  @Test
  public void getJavaFileOutputIsNotForwardedToStandardManagerForClassOutput() throws Exception {
    manager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, "className", Kind.CLASS, null);
    verifyZeroInteractions(sfm);
  }

  @Test
  public void getJavaFileOutputReportsProblemWhenClassNameIsIllegal() throws Exception {
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
