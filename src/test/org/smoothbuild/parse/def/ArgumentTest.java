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
import static org.smoothbuild.parse.def.Argument.namedArg;
import static org.smoothbuild.parse.def.Argument.namelessArg;
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
  public void nullNameIsForbiddenInNamedArg() {
    namedArg(null, node, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullDefinitionNodeIsForbiddenInNamedArg() {
    namedArg(name, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullDefinitionNodeIsForbiddenInNamelessArg() {
    namelessArg(null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbiddenInNamedArg() {
    namedArg(name, node, null);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbiddenInNamelessArg() {
    namelessArg(node, null);
  }

  @Test
  public void typeReturnsTypeOfDefinitionNode() throws Exception {
    when(node.type()).thenReturn(FILE);
    Type arg = namedArg(name, node, codeLocation).type();
    assertThat(arg).isEqualTo(FILE);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void namelessArgThrowsExceptionWhenAskedForName() throws Exception {
    nameless().name();
  }

  @Test
  public void namedArgHasName() throws Exception {
    assertThat(namedArg(name, node, codeLocation).hasName()).isTrue();
  }

  @Test
  public void namelessArgDoesNotHaveName() throws Exception {
    assertThat(namelessArg(node, codeLocation).hasName()).isFalse();
  }

  @Test
  public void sanitizedNamedOfNamedArg() throws Exception {
    assertThat(named(name).nameSanitized()).isEqualTo(name);
  }

  @Test
  public void sanitizedNamedOfNamelessArg() throws Exception {
    assertThat(nameless().nameSanitized()).isEqualTo("<nameless>");
  }

  @Test
  public void namedArgToString() throws Exception {
    when(node.type()).thenReturn(STRING);
    assertThat(namedArg(name, node, codeLocation).toString()).isEqualTo("String:" + name);
  }

  @Test
  public void namelessArgToString() throws Exception {
    when(node.type()).thenReturn(STRING);
    assertThat(namelessArg(node, codeLocation).toString()).isEqualTo("String:<nameless>");
  }

  @Test
  public void toPaddedString() throws Exception {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(STRING);

    Argument arg = namedArg("myName", node, codeLocation(1, 2, 3));
    String actual = arg.toPaddedString(10, 13);

    assertThat(actual).isEqualTo("String    : myName        [1:2-3]");
  }

  @Test
  public void toPaddedStringForShortLimits() throws Exception {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(STRING);

    Argument arg = namedArg("myName", node, codeLocation(1, 2, 3));
    String actual = arg.toPaddedString(1, 1);

    assertThat(actual).isEqualTo("String: myName [1:2-3]");
  }

  @Test
  public void filterNamed() throws Exception {
    Argument named1 = named("name1");
    Argument named2 = named("name2");
    Argument nameless1 = nameless();
    Argument nameless2 = nameless();

    ImmutableList<Argument> actual = Argument.filterNamed(ImmutableList.of(named1, named2,
        nameless1, nameless2));

    assertThat(actual).containsOnly(named1, named2);
  }

  @Test
  public void filterNameless() throws Exception {
    doTestFilterNameless(STRING);
    doTestFilterNameless(STRING_SET);
    doTestFilterNameless(FILE);
    doTestFilterNameless(FILE_SET);
    doTestFilterNameless(VOID);
    doTestFilterNameless(EMPTY_SET);
  }

  private void doTestFilterNameless(Type type) {
    Argument named1 = named("name1");
    Argument named2 = named("name2");
    Argument nameless = nameless(type);

    ImmutableMap<Type, Set<Argument>> actual = Argument.filterNameless(ImmutableList.of(named1,
        named2, nameless));

    assertThat(actual.get(type)).containsOnly(nameless);
  }

  private static Argument named(String name) {
    return Argument.namedArg(name, mock(DefinitionNode.class), codeLocation(1, 2, 3));
  }

  private static Argument nameless() {
    return nameless(Type.STRING);
  }

  private static Argument nameless(Type type) {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(type);
    return Argument.namelessArg(node, codeLocation(1, 2, 3));
  }
}
