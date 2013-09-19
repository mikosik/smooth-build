package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.google.common.collect.ImmutableList;

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
    String content = "content";
    String className = ".illegal.MyClass";

    JavaFileObject javaFile = manager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT,
        className, Kind.CLASS, null);
    StreamTester.writeAndClose(javaFile.openOutputStream(), content);

    assertThat(javaFile).isInstanceOf(DummyOutputClassFile.class);
    sandbox.messages().assertOnlyProblem(IncorrectClassNameGivenByJavaCompilerError.class);
    verifyZeroInteractions(sfm);
  }
}
