package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.function.base.Type;

public class EmptySetNodeTest {
  EmptySetNode emptySetNode = new EmptySetNode();

  @Test
  public void type() {
    assertThat(emptySetNode.type()).isEqualTo(Type.EMPTY_SET);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void generateTaskThrowsException() throws Exception {
    emptySetNode.generateTask();
  }
}
