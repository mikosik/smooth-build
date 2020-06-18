package org.smoothbuild.parse.deps;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.common.TestingLocation.loc;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.parse.ast.Named;

import com.google.common.collect.ImmutableSet;

public class DependencyStackTest {
  @Test
  public void stack_is_empty_initially() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    assertThat(dependencyStack.isEmpty())
        .isTrue();
  }

  @Test
  public void stack_is_not_empty_after_pushing_one_element() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    dependencyStack.push(elem());
    assertThat(dependencyStack.isEmpty())
        .isFalse();
  }

  @Test
  public void stack_is_empty_after_pushing_and_popping() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    dependencyStack.push(elem());
    dependencyStack.pop();
    assertThat(dependencyStack.isEmpty())
        .isTrue();
  }

  @Test
  public void pushed_element_is_returned_by_pop() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    StackElem<Named> elem = elem();
    dependencyStack.push(elem);
    assertThat(dependencyStack.pop())
        .isEqualTo(elem);
  }

  @Test
  public void elements_are_poped_in_filo_order() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    StackElem<Named> elem1 = elem();
    StackElem<Named> elem2 = elem();
    StackElem<Named> elem3 = elem();
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
  public void popping_from_empty_stack_throws_exception() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    assertCall(dependencyStack::pop)
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void popping_after_all_elements_has_been_removed_throws_exception() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    dependencyStack.push(elem());
    dependencyStack.pop();
    assertCall(dependencyStack::pop)
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void peek_returns_top_element() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    dependencyStack.push(elem());
    StackElem<Named> elem2 = elem();
    dependencyStack.push(elem2);
    assertThat(dependencyStack.peek())
        .isSameInstanceAs(elem2);
  }

  @Test
  public void peek_does_not_remove_element() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    StackElem<Named> elem1 = elem();
    dependencyStack.push(elem1);
    dependencyStack.peek();
    assertThat(dependencyStack.pop())
        .isSameInstanceAs(elem1);
  }

  @Test
  public void peeking_on_empty_stack_throws_exception() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    assertCall(dependencyStack::peek)
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void peeking_after_all_elements_has_been_removed_throws_exception() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    dependencyStack.push(elem());
    dependencyStack.pop();
    assertCall(dependencyStack::peek)
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void create_cycle_error() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    dependencyStack.push(elem("name1", "name2", 1));
    dependencyStack.push(elem("name2", "name3", 2));
    dependencyStack.push(elem("name3", "name4", 3));
    dependencyStack.push(elem("name4", "name2", 4));
    assertThat(dependencyStack.createCycleError().message())
        .isEqualTo("my stack contains cycle:\n"
            + "script.smooth:2: name2 -> name3\n"
            + "script.smooth:3: name3 -> name4\n"
            + "script.smooth:4: name4 -> name2\n");
  }

  @Test
  public void create_cycle_error_for_recursive_call() {
    DependencyStack<Named> dependencyStack = new DependencyStack<>("my stack");
    dependencyStack.push(elem("name1", "name2", 1));
    dependencyStack.push(elem("name2", "name2", 2));
    assertThat(dependencyStack.createCycleError().message())
        .isEqualTo("my stack contains cycle:\n"
            + "script.smooth:2: name2 -> name2\n");
  }

  private StackElem<Named> elem(String from, String to, int lineNumber) {
    StackElem<Named> elem = new StackElem<>(named(from), ImmutableSet.of());
    elem.setMissing(new MyNamed(to, loc(lineNumber)));
    return elem;
  }

  private static StackElem<Named> elem() {
    return new StackElem<>(named("name"), ImmutableSet.of());
  }

  public static Named named(String name) {
    return new MyNamed(name, Location.unknownLocation());
  }

  private static record MyNamed(String name, Location location) implements Named {
  }
}
