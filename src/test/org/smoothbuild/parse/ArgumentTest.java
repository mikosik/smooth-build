package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.problem.CodeLocation;

public class ArgumentTest {
  String name = "name";
  DefinitionNode node = mock(DefinitionNode.class);
  CodeLocation codeLocation = mock(CodeLocation.class);

  @Test(expected = NullPointerException.class)
  public void nullDefinitionNodeIsForbidden() {
    new Argument(name, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbidden() {
    new Argument(name, node, null);
  }

  @Test
  public void argumentWithNonNullNameIsExplicit() throws Exception {
    assertThat(new Argument(name, node, codeLocation).isExplicit()).isTrue();
  }

  @Test
  public void argumentWithNullNameIsNotExplicit() throws Exception {
    assertThat(new Argument(null, node, codeLocation).isExplicit()).isFalse();
  }
}
