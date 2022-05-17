package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.type.MergingTS.merge;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.type.TestingTS;

public class MergingTSTest extends TestingTS {
  @Test
  public void merge_up() {
    assertThat(merge(string(), bool(), UPPER))
        .isEqualTo(join(string(), bool()));
  }

  @Test
  public void merge_down() {
    assertThat(merge(string(), bool(), LOWER))
        .isEqualTo(meet(string(), bool()));
  }
}
