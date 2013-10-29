package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class InvalidNodeTest {
  InvalidNode invalidNode = new InvalidNode(Type.STRING, new FakeCodeLocation());

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() throws Exception {
    new InvalidNode(null, new FakeCodeLocation());
  }

  @Test
  public void type() {
    assertThat(invalidNode.type()).isEqualTo(Type.STRING);
  }

  @Test(expected = RuntimeException.class)
  public void generateTaskThrowsException() throws Exception {
    invalidNode.generateTask();
  }
}
