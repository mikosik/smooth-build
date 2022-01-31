package org.smoothbuild.slib.java.junit;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.slib.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.JarTester;

public class BinaryNameToClassFileTest extends TestingContext {
  @Test
  public void binary_names_are_mapped_to_proper_class_files() throws IOException, JunitExc {
    TupleB file1 = fileB(path("a/Klass.class"));
    TupleB file2 = fileB(path("b/Klass.class"));
    BlobB blob = JarTester.jar(file1, file2);
    assertThat(binaryNameToClassFile(nativeApi(), list(blob)))
        .containsExactly("a.Klass", file1, "b.Klass", file2);
  }

  @Test
  public void non_class_files_are_not_mapped() throws IOException, JunitExc {
    TupleB file1 = fileB(path("a/Klass.txt"));
    TupleB file2 = fileB(path("b/Klass.java"));
    BlobB blob = JarTester.jar(file1, file2);
    assertThat(binaryNameToClassFile(nativeApi(), list(blob)).entrySet())
        .isEmpty();
  }
}
