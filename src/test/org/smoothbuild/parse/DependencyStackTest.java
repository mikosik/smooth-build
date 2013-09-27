package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.CodeLocation.codeLocation;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.parse.err.CycleInCallGraphError;

import com.google.common.collect.ImmutableSet;

public class DependencyStackTest {
  DependencyStack dependencyStack = new DependencyStack();

  @Test
  public void stackIsEmptyInitially() {
    assertThat(dependencyStack.isEmpty()).isTrue();
  }

  @Test
  public void stackIsNotEmptyAfterPushingElement() throws Exception {
    dependencyStack.push(elem());
    assertThat(dependencyStack.isEmpty()).isFalse();
  }

  @Test
  public void stackIsEmptyAfterPushAndPop() {
    dependencyStack.push(elem());
    dependencyStack.pop();
    assertThat(dependencyStack.isEmpty()).isTrue();
  }

  @Test
  public void pushedElemIsReturnedByPoped() throws Exception {
    DependencyStackElem elem = elem();
    dependencyStack.push(elem);
    assertThat(dependencyStack.pop()).isSameAs(elem);
  }

  @Test
  public void elementsArePopedInFiloOrder() throws Exception {
    DependencyStackElem elem1 = elem();
    DependencyStackElem elem2 = elem();
    DependencyStackElem elem3 = elem();

    dependencyStack.push(elem1);
    dependencyStack.push(elem2);
    dependencyStack.push(elem3);

    assertThat(dependencyStack.pop()).isSameAs(elem3);
    assertThat(dependencyStack.pop()).isSameAs(elem2);
    assertThat(dependencyStack.pop()).isSameAs(elem1);
  }

  @Test
  public void popingFromEmptyStackThrowsException() throws Exception {
    try {
      dependencyStack.pop();
      Assert.fail("exception should be thrown");
    } catch (NoSuchElementException e) {
      // expected
    }
  }

  @Test
  public void popingAfterRemovingLastElemThrowsException() throws Exception {
    try {
      dependencyStack.push(elem());
      dependencyStack.pop();

      dependencyStack.pop();
      Assert.fail("exception should be thrown");
    } catch (NoSuchElementException e) {
      // expected
    }
  }

  @Test
  public void peekReturnsTopElem() throws Exception {
    DependencyStackElem elem1 = elem();
    DependencyStackElem elem2 = elem();

    dependencyStack.push(elem1);
    dependencyStack.push(elem2);

    assertThat(dependencyStack.peek()).isSameAs(elem2);
  }

  @Test
  public void peekDoesNotRemoveElement() throws Exception {
    DependencyStackElem elem1 = elem();
    dependencyStack.push(elem1);

    dependencyStack.peek();

    assertThat(dependencyStack.pop()).isSameAs(elem1);
  }

  @Test
  public void peekingFromEmptyStackThrowsException() throws Exception {
    try {
      dependencyStack.peek();
      Assert.fail("exception should be thrown");
    } catch (NoSuchElementException e) {
      // expected
    }
  }

  @Test
  public void peekingAfterRemovingLastElemThrowsException() throws Exception {
    try {
      dependencyStack.push(elem());
      dependencyStack.pop();

      dependencyStack.peek();
      Assert.fail("exception should be thrown");
    } catch (NoSuchElementException e) {
      // expected
    }
  }

  @Test
  public void createCycleError() throws Exception {
    dependencyStack.push(elem("name1", "name2", 1));
    dependencyStack.push(elem("name2", "name3", 2));
    dependencyStack.push(elem("name3", "name4", 3));
    dependencyStack.push(elem("name4", "name2", 4));

    CycleInCallGraphError error = dependencyStack.createCycleError();

    StringBuilder builder = new StringBuilder();
    builder.append("Function call graph contains cycle:\n");
    builder.append("name2[3:3-3] -> name3\n");
    builder.append("name3[4:4-4] -> name4\n");
    builder.append("name4[5:5-5] -> name2\n");

    assertThat(error.message()).isEqualTo(builder.toString());
  }

  @Test
  public void createCycleErrorForRecursiveCall() throws Exception {
    dependencyStack.push(elem("name1", "name2", 1));
    dependencyStack.push(elem("name2", "name2", 2));

    CycleInCallGraphError error = dependencyStack.createCycleError();

    StringBuilder builder = new StringBuilder();
    builder.append("Function call graph contains cycle:\n");
    builder.append("name2[3:3-3] -> name2\n");

    assertThat(error.message()).isEqualTo(builder.toString());
  }

  private DependencyStackElem elem(String from, String to, int location) {
    ImmutableSet<Dependency> deps = ImmutableSet.of();
    DependencyStackElem elem = new DependencyStackElem(from, deps);
    elem.setMissing(new Dependency(codeLocation(location, location, location), to));
    return elem;
  }

  private static DependencyStackElem elem() {
    return new DependencyStackElem("name", ImmutableSet.<Dependency> of());
  }
}
