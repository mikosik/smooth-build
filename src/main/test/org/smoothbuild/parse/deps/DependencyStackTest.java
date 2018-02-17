package org.smoothbuild.parse.deps;

import static org.hamcrest.core.IsSame.sameInstance;
import static org.smoothbuild.lang.message.Location.location;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.smoothbuild.parse.ParseError;
import org.smoothbuild.parse.ast.NamedNode;

import com.google.common.collect.ImmutableSet;

public class DependencyStackTest {
  private final String name1 = "function1";
  private final String name2 = "function2";
  private final String name3 = "function3";
  private final String name4 = "function4";
  private final String stackName = "My Stack";

  private DependencyStack dependencyStack;
  private StackElem elem1;
  private StackElem elem2;
  private StackElem elem3;

  @Test
  public void stack_is_empty_initially() {
    given(dependencyStack = new DependencyStack(stackName));
    when(dependencyStack).isEmpty();
    thenReturned(true);
  }

  @Test
  public void stack_is_not_empty_after_pushing_one_element() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(dependencyStack).push(elem());
    when(dependencyStack).isEmpty();
    thenReturned(false);
  }

  @Test
  public void stack_is_empty_after_pushing_and_popping() {
    given(dependencyStack = new DependencyStack(stackName));
    given(dependencyStack).push(elem());
    given(dependencyStack).pop();
    when(dependencyStack).isEmpty();
    thenReturned(true);
  }

  @Test
  public void pushed_element_is_returned_by_pop() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(elem1 = elem());
    given(dependencyStack).push(elem1);
    when(dependencyStack).pop();
    thenReturned(elem1);
  }

  @Test
  public void elements_are_poped_in_filo_order() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(elem1 = elem());
    given(elem2 = elem());
    given(elem3 = elem());
    given(dependencyStack).push(elem1);
    given(dependencyStack).push(elem2);
    given(dependencyStack).push(elem3);
    when(dependencyStack).pop();
    thenReturned(sameInstance(elem3));
    when(dependencyStack).pop();
    thenReturned(sameInstance(elem2));
    when(dependencyStack).pop();
    thenReturned(sameInstance(elem1));
  }

  @Test
  public void poping_from_empty_stack_throws_exception() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    when(dependencyStack).pop();
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void poping_after_all_elements_has_been_removed_throws_exception() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(dependencyStack).push(elem());
    given(dependencyStack).pop();
    when(dependencyStack).pop();
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void peek_returns_top_element() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(dependencyStack).push(elem());
    given(elem2 = elem());
    given(dependencyStack).push(elem2);
    when(dependencyStack).peek();
    thenReturned(sameInstance(elem2));
  }

  @Test
  public void peek_does_not_remove_element() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(elem1 = elem());
    given(dependencyStack).push(elem1);
    when(dependencyStack).peek();
    thenEqual(dependencyStack.pop(), elem1);
  }

  @Test
  public void peeking_on_empty_stack_throws_exception() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    when(dependencyStack).peek();
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void peeking_after_all_elements_has_been_removed_throws_exception() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(dependencyStack).push(elem());
    given(dependencyStack).pop();
    when(dependencyStack).peek();
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void create_cycle_error() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(dependencyStack).push(elem(name1, name2, 1));
    given(dependencyStack).push(elem(name2, name3, 2));
    given(dependencyStack).push(elem(name3, name4, 3));
    given(dependencyStack).push(elem(name4, name2, 4));
    when(() -> dependencyStack.createCycleError().toString());
    thenReturned(new ParseError(location(Paths.get("script.smooth"), 2),
        "My Stack contains cycle:\n"
            + name2 + location(Paths.get("script.smooth"), 2) + " -> " + name3 + "\n"
            + name3 + location(Paths.get("script.smooth"), 3) + " -> " + name4 + "\n"
            + name4 + location(Paths.get("script.smooth"), 4) + " -> " + name2 + "\n").toString());
  }

  @Test
  public void create_cycle_error_for_recursive_call() throws Exception {
    given(dependencyStack = new DependencyStack(stackName));
    given(dependencyStack).push(elem(name1, name2, 1));
    given(dependencyStack).push(elem(name2, name2, 2));
    when(() -> dependencyStack.createCycleError().toString());
    thenReturned(new ParseError(location(Paths.get("script.smooth"), 2),
        "My Stack contains cycle:\n"
            + name2 + location(Paths.get("script.smooth"), 2) + " -> " + name2 + "\n").toString());
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
