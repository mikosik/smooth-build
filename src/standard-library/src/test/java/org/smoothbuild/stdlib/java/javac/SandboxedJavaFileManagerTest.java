package org.smoothbuild.stdlib.java.javac;

import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.ArrayList;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class SandboxedJavaFileManagerTest extends TestVirtualMachine {
  @Test
  public void getJavaFile_output_is_not_forwarded_to_standard_manager_for_class_output()
      throws Exception {
    Iterable<InputClassFile> objects = new ArrayList<>();
    StandardJavaFileManager sfm = mock(StandardJavaFileManager.class);
    SandboxedJavaFileManager manager = new SandboxedJavaFileManager(sfm, nativeApi(), objects);
    manager.getJavaFileForOutput(CLASS_OUTPUT, "className", Kind.CLASS, null);
    verifyNoInteractions(sfm);
  }
}
