package org.smoothbuild.builtin.java.javac;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;

import java.util.ArrayList;

import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

import org.junit.Test;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.task.exec.TestingContainer;

public class SandboxedJavaFileManagerTest {
  private final StandardJavaFileManager sfm = mock(StandardJavaFileManager.class);
  private final NativeApi nativeApi = new TestingContainer();
  private final Iterable<InputClassFile> packagedJavaFileObjects = new ArrayList<>();
  private SandboxedJavaFileManager manager;

  @Test
  public void getJavaFile_output_is_not_forwarded_to_standard_manager_for_class_output()
      throws Exception {
    given(manager = new SandboxedJavaFileManager(sfm, nativeApi, packagedJavaFileObjects));
    when(manager).getJavaFileForOutput(CLASS_OUTPUT, "className", Kind.CLASS, null);
    thenCalledTimes(0, onInstance(sfm));
  }
}
