package org.smoothbuild.virtualmachine.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;

public class TraceBTest {
  @Test
  public void to_string() {
    var trace = new TraceB(Hash.of(7), Hash.of(9), new TraceB(Hash.of(17), Hash.of(19)));
    assertThat(trace.toString())
        .isEqualTo(
            """
            e8613f5a5bc9f9feeda32a8e7c80b69dd4878e47b6a91723fb15eb84236b6a2b 9f076b7eb7fdc0311cd3208cdbbebbf8014dd3a05e35191c96947b358a362b40
            84fc05949dc1e486652a4ed316afb6434e9437eb30b714594a1d0b4205776602 eba09f2f48f209cfa2dfbf19fc678d755d05559671eceda0164f3e080cb49765""");
  }
}
