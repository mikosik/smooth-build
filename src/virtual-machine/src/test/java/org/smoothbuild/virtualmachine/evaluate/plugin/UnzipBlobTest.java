package org.smoothbuild.virtualmachine.evaluate.plugin;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.virtualmachine.evaluate.plugin.UnzipBlob.unzipBlob;
import static org.smoothbuild.virtualmachine.testing.JarTester.jar;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class UnzipBlobTest extends VmTestContext {
  @Test
  void unzip_blob() throws Exception {
    BTuple file1 = bFile("file1.txt", "abc");
    BTuple file2 = bFile("file2.txt", "def");
    assertThat(unzipBlob(provide().bytecodeFactory(), jar(file1, file2), f -> true))
        .isEqualTo(ok(bArray(file1, file2)));
  }
}
