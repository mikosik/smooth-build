package org.smoothbuild.virtualmachine.evaluate.plugin;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.virtualmachine.evaluate.plugin.UnzipBlob.unzipBlob;
import static org.smoothbuild.virtualmachine.testing.JarTester.jar;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class UnzipBlobTest extends TestVirtualMachine {
  @Test
  public void unzip_blob() throws Exception {
    TupleB file1 = fileB("file1.txt", "abc");
    TupleB file2 = fileB("file2.txt", "def");
    assertThat(unzipBlob(bytecodeF(), jar(file1, file2), f -> true))
        .isEqualTo(right(arrayB(file1, file2)));
  }
}