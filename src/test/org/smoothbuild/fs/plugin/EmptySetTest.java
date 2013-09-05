package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EmptySetTest {

  @Test
  public void onlyOneInstanceOfEmptySetExists() {
    assertThat(EmptySet.emptySet()).isSameAs(EmptySet.emptySet());
  }

}
