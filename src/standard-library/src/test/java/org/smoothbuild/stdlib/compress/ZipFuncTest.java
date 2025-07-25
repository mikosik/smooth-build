package org.smoothbuild.stdlib.compress;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.testing.TestingThread.sleepMillis;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class ZipFuncTest extends VmTestContext {
  @Test
  void zip_produces_bit_level_equal_file_independent_of_its_creation_time() throws Exception {
    var fileArray = bTuple(bArray(bFile("filename", "context")));
    var zip1 = ZipFunc.func(provide().container(), fileArray);
    sleepMillis(2000);
    var zip2 = ZipFunc.func(provide().container(), fileArray);
    assertThat(zip1.hash()).isEqualTo(zip2.hash());
  }
}
