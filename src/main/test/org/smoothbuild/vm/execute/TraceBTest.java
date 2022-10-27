package org.smoothbuild.vm.execute;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;

public class TraceBTest {
  @Test
  public void to_string() {
    var trace = new TraceB(Hash.of(7), Hash.of(9), new TraceB(Hash.of(17), Hash.of(19)));
    assertThat(trace.toString())
        .isEqualTo("""
            2f086fc767a0dac59a38c67f409b4f74a1eab39f 9db063f3b5e0adfd0d29a03db0a1c207b3740a94
            498bcbf6cbffcc8dd2623f388d81f44cfad1014d fe5aa6438ae9b661b033b91e9c679ad2898cbfd4""");
  }
}
