package org.smoothbuild.stdlib.java;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class JarFuncTest extends TestingVirtualMachine {
  @Test
  public void jar_produces_bit_level_equal_file_independent_of_its_creation_time()
      throws Exception {
    var args = tupleB(arrayB(fileB("filename", "context")), blobB(37));
    var zip1 = JarFunc.func(nativeApi(), args);
    sleepMillis(2000);
    var zip2 = JarFunc.func(nativeApi(), args);
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
