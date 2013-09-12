package org.smoothbuild.parse.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.parse.def.Argument.explicitArg;
import static org.smoothbuild.parse.def.Argument.implicitArg;
import static org.smoothbuild.problem.CodeLocation.codeLocation;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.problem.CodeLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ArgumentTest {
  String name = "name";
  DefinitionNode node = mock(DefinitionNode.class);
  CodeLocation codeLocation = mock(CodeLocation.class);

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbiddenInExplicitArg() {
    explicitArg(null, node, codeLocation);
  }

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
  public void typeReturnsTypeOfDefinitionNode() throws Exception {
    when(node.type()).thenReturn(FILE);
    Type arg = explicitArg(name, node, codeLocation).type();
    assertThat(arg).isEqualTo(FILE);
  }

  @Test
  public void explicitArgIsExplicit() throws Exception {
    assertThat(explicitArg(name, node, codeLocation).isExplicit()).isTrue();
  }

  @Test
  public void implicitArgIsNotExplicit() throws Exception {
    assertThat(implicitArg(node, codeLocation).isExplicit()).isFalse();
  }

  @Test
  public void filterExplicit() throws Exception {
    Argument explicit1 = explicit("name1");
    Argument explicit2 = explicit("name2");
    Argument implicit1 = implicit();
    Argument implicit2 = implicit();

    ImmutableList<Argument> actual = Argument.filterExplicit(ImmutableList.of(explicit1, explicit2,
        implicit1, implicit2));

    assertThat(actual).containsOnly(explicit1, explicit2);
  }

  @Test
  public void filterImplicit() throws Exception {
    doTestFilterImplicit(STRING);
    doTestFilterImplicit(STRING_SET);
    doTestFilterImplicit(FILE);
    doTestFilterImplicit(FILE_SET);
    doTestFilterImplicit(VOID);
    doTestFilterImplicit(EMPTY_SET);
  }

  private void doTestFilterImplicit(Type type) {
    Argument explicit1 = explicit("name1");
    Argument explicit2 = explicit("name2");
    Argument implicit = implicit(type);

    ImmutableMap<Type, Set<Argument>> actual = Argument.filterImplicit(ImmutableList.of(explicit1,
        explicit2, implicit));

    assertThat(actual.get(type)).containsOnly(implicit);
  }

  private static Argument explicit(String name) {
    return Argument.explicitArg(name, mock(DefinitionNode.class), codeLocation(1, 2, 3));
  }

  private static Argument implicit() {
    return implicit(Type.STRING);
  }

  private static Argument implicit(Type type) {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(type);
    return Argument.implicitArg(node, codeLocation(1, 2, 3));
  }
}
