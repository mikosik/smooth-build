package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.function.def.args.Argument.namedArg;
import static org.smoothbuild.lang.function.def.args.Argument.namelessArg;
import static org.smoothbuild.lang.function.def.args.Argument.pipedArg;
import static org.smoothbuild.lang.type.Type.EMPTY_SET;
import static org.smoothbuild.lang.type.Type.FILE;
import static org.smoothbuild.lang.type.Type.FILE_SET;
import static org.smoothbuild.lang.type.Type.STRING;
import static org.smoothbuild.lang.type.Type.STRING_SET;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.message.FakeCodeLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ArgumentTest {
  String name = "name";
  Node node = mock(Node.class);
  CodeLocation codeLocation = new FakeCodeLocation();

  @Test(expected = IllegalArgumentException.class)
  public void negativeIndexIsForbiddenInNamedArg() {
    namedArg(-1, name, node, codeLocation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zeroIndexIsForbiddenInNamedArg() {
    namedArg(0, name, node, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbiddenInNamedArg() {
    namedArg(1, null, node, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullNodeIsForbiddenInNamedArg() {
    namedArg(1, name, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbiddenInNamedArg() {
    namedArg(1, name, node, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeIndexIsForbiddenInNamelessArg() {
    namelessArg(-1, node, codeLocation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zeroIndexIsForbiddenInNamelessArg() {
    namelessArg(0, node, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullNodeIsForbiddenInNamelessArg() {
    namelessArg(1, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbiddenInNamelessArg() {
    namelessArg(1, node, null);
  }

  @Test(expected = NullPointerException.class)
  public void nullNodeIsForbiddenInPipedArg() {
    pipedArg(null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullSourceLocationIsForbiddenInPipedArg() {
    pipedArg(node, null);
  }

  @Test
  public void typeReturnsTypeOfNode() throws Exception {
    when(node.type()).thenReturn(FILE);
    Type arg = namedArg(1, name, node, codeLocation).type();
    assertThat(arg).isEqualTo(FILE);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void namelessArgThrowsExceptionWhenAskedForName() throws Exception {
    nameless().name();
  }

  @Test
  public void namedArgHasName() throws Exception {
    assertThat(namedArg(1, name, node, codeLocation).hasName()).isTrue();
  }

  @Test
  public void namelessArgDoesNotHaveName() throws Exception {
    assertThat(namelessArg(1, node, codeLocation).hasName()).isFalse();
  }

  @Test
  public void pipedArgDoesNotHaveName() throws Exception {
    assertThat(pipedArg(node, codeLocation).hasName()).isFalse();
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
    assertThat(namedArg(1, name, node, codeLocation).toString()).isEqualTo("String:" + name);
  }

  @Test
  public void namelessArgToString() throws Exception {
    when(node.type()).thenReturn(STRING);
    assertThat(namelessArg(1, node, codeLocation).toString()).isEqualTo("String:<nameless>");
  }

  @Test
  public void toPaddedString() throws Exception {
    when(node.type()).thenReturn(STRING);

    Argument arg = namedArg(1, "myName", node, codeLocation);
    String actual = arg.toPaddedString(10, 13, 7);

    assertThat(actual).isEqualTo("String    : myName        #1       " + codeLocation.toString());
  }

  @Test
  public void toPaddedStringForShortLimits() throws Exception {
    when(node.type()).thenReturn(STRING);

    Argument arg = namedArg(1, "myName", node, new FakeCodeLocation());
    String actual = arg.toPaddedString(1, 1, 1);

    assertThat(actual).isEqualTo("String: myName #1 " + codeLocation.toString());
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
    return Argument.namedArg(1, name, mock(Node.class), new FakeCodeLocation());
  }

  private static Argument nameless() {
    return nameless(Type.STRING);
  }

  private static Argument nameless(Type type) {
    Node node = mock(Node.class);
    when(node.type()).thenReturn(type);
    return Argument.namelessArg(1, node, new FakeCodeLocation());
  }
}
