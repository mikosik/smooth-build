package org.smoothbuild.slib.java.junit;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.JarTester;

public class BinaryNameToClassFileTest extends TestingContext {
  @Test
  public void binary_names_are_mapped_to_proper_class_files() throws IOException, JunitExc {
    TupleH file1 = fileH(path("a/Klass.class"));
    TupleH file2 = fileH(path("b/Klass.class"));
    BlobH blob = JarTester.jar(file1, file2);
    assertThat(binaryNameToClassFile(nativeApi(), list(blob)))
        .containsExactly("a.Klass", file1, "b.Klass", file2);
  }

  @Test
  public void non_class_files_are_not_mapped() throws IOException, JunitExc {
    TupleH file1 = fileH(path("a/Klass.txt"));
    TupleH file2 = fileH(path("b/Klass.java"));
    BlobH blob = JarTester.jar(file1, file2);
    assertThat(binaryNameToClassFile(nativeApi(), list(blob)).entrySet())
        .isEmpty();
  }
}
