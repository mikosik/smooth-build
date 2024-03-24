package org.smoothbuild.stdlib.compress;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ZipFuncTest extends TestingVirtualMachine {
  @Test
  public void zip_produces_bit_level_equal_file_independent_of_its_creation_time()
      throws Exception {
    var fileArray = bTuple(bArray(bFile("filename", "context")));
    var zip1 = ZipFunc.func(nativeApi(), fileArray);
    sleepMillis(2000);
    var zip2 = ZipFunc.func(nativeApi(), fileArray);
    assertThat(zip1.hash()).isEqualTo(zip2.hash());
  }

  private static void sleepMillis(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
