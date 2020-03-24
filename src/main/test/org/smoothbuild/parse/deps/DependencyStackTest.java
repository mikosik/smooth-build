package org.smoothbuild.parse.deps;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.parse.ParseError;
import org.smoothbuild.parse.ast.NamedNode;

import com.google.common.collect.ImmutableSet;

public class DependencyStackTest {
  @Test
  public void stack_is_empty_initially() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    assertThat(dependencyStack.isEmpty())
        .isTrue();
  }

  @Test
  public void stack_is_not_empty_after_pushing_one_element() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    dependencyStack.push(elem());
    assertThat(dependencyStack.isEmpty())
        .isFalse();
  }

  @Test
  public void stack_is_empty_after_pushing_and_popping() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    dependencyStack.push(elem());
    dependencyStack.pop();
    assertThat(dependencyStack.isEmpty())
        .isTrue();
  }

  @Test
  public void pushed_element_is_returned_by_pop() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    StackElem elem = elem();
    dependencyStack.push(elem);
    assertThat(dependencyStack.pop())
        .isEqualTo(elem);
  }

  @Test
  public void elements_are_poped_in_filo_order() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    StackElem elem1 = elem();
    StackElem elem2 = elem();
    StackElem elem3 = elem();
    dependencyStack.push(elem1);
    dependencyStack.push(elem2);
    dependencyStack.push(elem3);
    assertThat(dependencyStack.pop())
        .isEqualTo(elem3);
    assertThat(dependencyStack.pop())
        .isEqualTo(elem2);
    assertThat(dependencyStack.pop())
        .isEqualTo(elem1);
  }

  @Test
  public void poping_from_empty_stack_throws_exception() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    assertCall(() -> dependencyStack.pop())
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void poping_after_all_elements_has_been_removed_throws_exception() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    dependencyStack.push(elem());
    dependencyStack.pop();
    assertCall(() -> dependencyStack.pop())
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void peek_returns_top_element() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    dependencyStack.push(elem());
    StackElem elem2 = elem();
    dependencyStack.push(elem2);
    assertThat(dependencyStack.peek())
        .isSameInstanceAs(elem2);
  }

  @Test
  public void peek_does_not_remove_element() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    StackElem elem1 = elem();
    dependencyStack.push(elem1);
    dependencyStack.peek();
    assertThat(dependencyStack.pop())
        .isSameInstanceAs(elem1);
  }

  @Test
  public void peeking_on_empty_stack_throws_exception() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    assertCall(dependencyStack::peek)
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void peeking_after_all_elements_has_been_removed_throws_exception() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    dependencyStack.push(elem());
    dependencyStack.pop();
    assertCall(dependencyStack::peek)
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void create_cycle_error() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    dependencyStack.push(elem("name1", "name2", 1));
    dependencyStack.push(elem("name2", "name3", 2));
    dependencyStack.push(elem("name3", "name4", 3));
    dependencyStack.push(elem("name4", "name2", 4));
    assertThat(dependencyStack.createCycleError().toString())
        .isEqualTo(new ParseError(location(Paths.get("script.smooth"), 2),
        "my stack contains cycle:\n"
            + "script.smooth:2: name2 -> name3\n"
            + "script.smooth:3: name3 -> name4\n"
            + "script.smooth:4: name4 -> name2\n").toString());
  }

  @Test
  public void create_cycle_error_for_recursive_call() {
    DependencyStack dependencyStack = new DependencyStack("my stack");
    dependencyStack.push(elem("name1", "name2", 1));
    dependencyStack.push(elem("name2", "name2", 2));
    assertThat(dependencyStack.createCycleError().toString())
        .isEqualTo(new ParseError(location(Paths.get("script.smooth"), 2),
        "my stack contains cycle:\n"
            + "script.smooth:2: name2 -> name2\n").toString());
  }

  private StackElem elem(String from, String to, int location) {
    StackElem elem = new StackElem(from, ImmutableSet.of());
    elem.setMissing(new NamedNode(to, location(Paths.get("script.smooth"), location)));
    return elem;
  }

  private static StackElem elem() {
    return new StackElem("name", ImmutableSet.of());
  }
}
