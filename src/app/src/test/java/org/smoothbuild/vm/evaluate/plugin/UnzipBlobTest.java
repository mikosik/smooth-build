package org.smoothbuild.vm.evaluate.plugin;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Either.right;
import static org.smoothbuild.testing.common.JarTester.jar;
import static org.smoothbuild.vm.evaluate.plugin.UnzipBlob.unzipBlob;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class UnzipBlobTest extends TestContext {
  @Test
  public void unzip_blob() throws Exception {
    TupleB file1 = fileB("file1.txt", "abc");
    TupleB file2 = fileB("file2.txt", "def");
    assertThat(unzipBlob(bytecodeF(), jar(file1, file2), f -> true))
        .isEqualTo(right(arrayB(file1, file2)));
  }
}
