package org.smoothbuild.parse.deps;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.smoothbuild.lang.message.Location.location;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.NoSuchElementException;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ParseError;
import org.smoothbuild.parse.ast.FuncNode;

import com.google.common.collect.ImmutableSet;

public class DependencyStackTest {
  private final Name name1 = new Name("function1");
  private final Name name2 = new Name("function2");
  private final Name name3 = new Name("function3");
  private final Name name4 = new Name("function4");

  private DependencyStack dependencyStack;
  private DependencyStackElem elem1;
  private DependencyStackElem elem2;
  private DependencyStackElem elem3;

  @Test
  public void stack_is_empty_initially() {
    given(dependencyStack = new DependencyStack());
    when(dependencyStack).isEmpty();
    thenReturned(true);
  }

  @Test
  public void stack_is_not_empty_after_pushing_one_element() throws Exception {
    given(dependencyStack = new DependencyStack());
    given(dependencyStack).push(elem());
    when(dependencyStack).isEmpty();
    thenReturned(false);
  }

  @Test
  public void stack_is_empty_after_pushing_and_popping() {
    given(dependencyStack = new DependencyStack());
    given(dependencyStack).push(elem());
    given(dependencyStack).pop();
    when(dependencyStack).isEmpty();
    thenReturned(true);
  }

  @Test
  public void pushed_element_is_returned_by_pop() throws Exception {
    given(dependencyStack = new DependencyStack());
    given(elem1 = elem());
    given(dependencyStack).push(elem1);
    when(dependencyStack).pop();
    thenReturned(elem1);
  }

  @Test
  public void elements_are_poped_in_filo_order() throws Exception {
    given(dependencyStack = new DependencyStack());
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
    given(dependencyStack = new DependencyStack());
    when(dependencyStack).pop();
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void poping_after_all_elements_has_been_removed_throws_exception() throws Exception {
    given(dependencyStack = new DependencyStack());
    given(dependencyStack).push(elem());
    given(dependencyStack).pop();
    when(dependencyStack).pop();
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void peek_returns_top_element() throws Exception {
    given(dependencyStack = new DependencyStack());
    given(dependencyStack).push(elem());
    given(elem2 = elem());
    given(dependencyStack).push(elem2);
    when(dependencyStack).peek();
    thenReturned(sameInstance(elem2));
  }

  @Test
  public void peek_does_not_remove_element() throws Exception {
    given(dependencyStack = new DependencyStack());
    given(elem1 = elem());
    given(dependencyStack).push(elem1);
    when(dependencyStack).peek();
    thenEqual(dependencyStack.pop(), elem1);
  }

  @Test
  public void peeking_on_empty_stack_throws_exception() throws Exception {
    given(dependencyStack = new DependencyStack());
    when(dependencyStack).peek();
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void peeking_after_all_elements_has_been_removed_throws_exception() throws Exception {
    given(dependencyStack = new DependencyStack());
    given(dependencyStack).push(elem());
    given(dependencyStack).pop();
    when(dependencyStack).peek();
    thenThrown(NoSuchElementException.class);
  }

  @Test
  public void create_cycle_error() throws Exception {
    given(dependencyStack = new DependencyStack());
    given(dependencyStack).push(elem(name1, name2, 1));
    given(dependencyStack).push(elem(name2, name3, 2));
    given(dependencyStack).push(elem(name3, name4, 3));
    given(dependencyStack).push(elem(name4, name2, 4));
    when(() -> dependencyStack.createCycleError().toString());
    thenReturned(new ParseError(location(2),
        "Function call graph contains cycle:\n"
            + name2 + location(2) + " -> " + name3 + "\n"
            + name3 + location(3) + " -> " + name4 + "\n"
            + name4 + location(4) + " -> " + name2 + "\n").toString());
  }

  @Test
  public void create_cycle_error_for_recursive_call() throws Exception {
    given(dependencyStack = new DependencyStack());
    given(dependencyStack).push(elem(name1, name2, 1));
    given(dependencyStack).push(elem(name2, name2, 2));
    when(() -> dependencyStack.createCycleError().toString());
    thenReturned(new ParseError(location(2), "Function call graph contains cycle:\n"
        + name2 + location(2) + " -> " + name2 + "\n").toString());
  }

  private DependencyStackElem elem(Name from, Name to, int location) {
    DependencyStackElem elem = new DependencyStackElem(
        new FuncNode(null, from, asList(), null, null), ImmutableSet.of());
    elem.setMissing(new Dependency(location(location), to));
    return elem;
  }

  private static DependencyStackElem elem() {
    return new DependencyStackElem(new FuncNode(
        null, new Name("name"), asList(), null, null), ImmutableSet.of());
  }
}
