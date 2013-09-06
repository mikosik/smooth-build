package org.smoothbuild.parse.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.parse.def.Argument.explicitArg;
import static org.smoothbuild.parse.def.Argument.implicitArg;

import org.junit.Test;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.problem.CodeLocation;

public class ArgumentTest {
  String name = "name";
  DefinitionNode node = mock(DefinitionNode.class);
  CodeLocation codeLocation = mock(CodeLocation.class);

  @Test(expected = NullPointerException.class)
  public void nullDefinitionNodeIsForbiddenInExplicitArg() {
    explicitArg(name, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullDefinitionNodeIsForbiddenInImplicitArg() {
    implicitArg(null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbiddenInExplicitArg() {
    explicitArg(name, node, null);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbiddenInImplicitArg() {
    implicitArg(node, null);
  }

  @Test
  public void explicitArgIsExplicit() throws Exception {
    assertThat(explicitArg(name, node, codeLocation).isExplicit()).isTrue();
  }

  @Test
  public void implicitArgIsNotExplicit() throws Exception {
    assertThat(implicitArg(node, codeLocation).isExplicit()).isFalse();
  }
}
