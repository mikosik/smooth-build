package org.smoothbuild.plugin;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.plugin.UnzipBlob.unzipBlob;
import static org.smoothbuild.testing.common.JarTester.jar;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.testing.TestContext;

public class UnzipBlobTest extends TestContext {
  @Test
  public void unzip_blob() throws Exception {
    TupleB file1 = fileB("file1.txt", "abc");
    TupleB file2 = fileB("file2.txt", "def");
    assertThat(unzipBlob(bytecodeF(), jar(file1, file2), f -> true))
        .isEqualTo(arrayB(file1, file2));
  }
}
