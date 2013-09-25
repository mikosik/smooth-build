package org.smoothbuild.builtin.java.javac;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.junit.Test;
import org.smoothbuild.builtin.java.javac.err.IncorrectClassNameGivenByJavaCompilerError;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.plugin.internal.FileTester;
import org.smoothbuild.testing.plugin.internal.TestSandbox;

public class SandboxedJavaFileManagerTest {
  StandardJavaFileManager sfm = mock(StandardJavaFileManager.class);
  TestSandbox sandbox = new TestSandbox();
  LibraryClasses libraryClasses = mock(LibraryClasses.class);

  SandboxedJavaFileManager manager = new SandboxedJavaFileManager(sfm, sandbox, libraryClasses);

  @Test
  public void getJavaFileOutput() throws IOException {
    String content = "content";
    String className = "my.package.MyClass";

    JavaFileObject javaFile = manager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT,
        className, Kind.CLASS, null);
    StreamTester.writeAndClose(javaFile.openOutputStream(), content);

    Path classFilePath = path("my/package/MyClass.class");
    FileTester.assertContentContains(sandbox.resultFileSet().file(classFilePath), content);
  }

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
    } catch (IncorrectClassNameGivenByJavaCompilerError e) {
      // expected
    }
    verifyZeroInteractions(sfm);
  }
}
