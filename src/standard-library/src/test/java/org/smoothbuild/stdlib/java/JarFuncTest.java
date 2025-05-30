package org.smoothbuild.stdlib.java;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.testing.TestingThread.sleepMillis;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class JarFuncTest extends VmTestContext {
  @Test
  void jar_produces_bit_level_equal_file_independent_of_its_creation_time() throws Exception {
    var args = bTuple(bArray(bFile("filename", "context")), bBlob(37));
    var zip1 = JarFunc.func(provide().container(), args);
    sleepMillis(2000);
    var zip2 = JarFunc.func(provide().container(), args);
    assertThat(zip1.hash()).isEqualTo(zip2.hash());
  }
}
