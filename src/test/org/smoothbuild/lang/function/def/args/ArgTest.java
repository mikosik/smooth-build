package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.lang.function.def.args.Arg.namelessArg;
import static org.smoothbuild.lang.function.def.args.Arg.pipedArg;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.message.FakeCodeLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ArgTest {
  String name = "name";
  Node<?> node = mock(Node.class);
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
    given(willReturn(FILE), node).type();
    when(namedArg(1, name, node, codeLocation)).type();
    thenReturned(FILE);
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
    given(willReturn(STRING), node).type();
    when(namedArg(1, name, node, codeLocation)).toString();
    thenReturned("String:" + name);
  }

  @Test
  public void namelessArgToString() throws Exception {
    given(willReturn(STRING), node).type();
    when(namelessArg(1, node, codeLocation)).toString();
    thenReturned("String:<nameless>");
  }

  @Test
  public void toPaddedString() throws Exception {
    given(willReturn(STRING), node).type();
    when(namedArg(1, "myName", node, codeLocation)).toPaddedString(10, 13, 7);
    thenReturned("String    : myName        #1       " + codeLocation.toString());
  }

  @Test
  public void toPaddedStringForShortLimits() throws Exception {
    given(willReturn(STRING), node).type();
    when(namedArg(1, "myName", node, new FakeCodeLocation())).toPaddedString(1, 1, 1);
    thenReturned("String: myName #1 " + codeLocation.toString());
  }

  @Test
  public void filterNamed() throws Exception {
    Arg named1 = named("name1");
    Arg named2 = named("name2");
    Arg nameless1 = nameless();
    Arg nameless2 = nameless();

    ImmutableList<Arg> actual =
        Arg.filterNamed(ImmutableList.of(named1, named2, nameless1, nameless2));

    assertThat(actual).containsOnly(named1, named2);
  }

  @Test
  public void filterNameless() throws Exception {
    doTestFilterNameless(STRING);
    doTestFilterNameless(STRING_ARRAY);
    doTestFilterNameless(FILE);
    doTestFilterNameless(FILE_ARRAY);
    doTestFilterNameless(EMPTY_ARRAY);
  }

  private void doTestFilterNameless(SType<?> type) {
    Arg named1 = named("name1");
    Arg named2 = named("name2");
    Arg nameless = nameless(type);

    ImmutableMap<SType<?>, Set<Arg>> actual =
        Arg.filterNameless(ImmutableList.of(named1, named2, nameless));

    assertThat(actual.get(type)).containsOnly(nameless);
  }

  private static Arg named(String name) {
    return Arg.namedArg(1, name, mock(Node.class), new FakeCodeLocation());
  }

  private static Arg nameless() {
    return nameless(STRING);
  }

  private static Arg nameless(SType<?> type) {
    Node<?> node = mock(Node.class);
    given(willReturn(type), node).type();
    return Arg.namelessArg(1, node, new FakeCodeLocation());
  }
}
