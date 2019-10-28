package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.testing.TestingContext;

public class InputTest extends TestingContext{
  private Task depTask1;
  private Task depTask2;
  private Input input;
  private Input input2;
  private SString sstring1;
  private SString sstring2;

  @Test
  public void input_takes_values_from_dependency_tasks() {
    given(depTask1 = mock(Task.class));
    given(sstring1 = string("abc"));
    given(willReturn(new Output(sstring1, emptyMessageArray())), depTask1).output();
    given(input = Input.fromResults(list(depTask1)));
    when(input).objects();
    thenReturned(contains(sstring1));
  }

  @Test
  public void different_inputs_have_different_hashes() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = string("abc"));
    given(sstring2 = string("def"));
    given(willReturn(new Output(sstring1, emptyMessageArray())), depTask1).output();
    given(willReturn(new Output(sstring2, emptyMessageArray())), depTask2).output();
    given(input = Input.fromResults(list(depTask1)));
    given(input2 = Input.fromResults(list(depTask2)));
    when(input).hash();
    thenReturned(not(input2.hash()));
  }

  @Test
  public void inputs_with_same_values_but_in_different_order_have_different_hashes()
      throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = string("abc"));
    given(sstring2 = string("def"));
    given(willReturn(new Output(sstring1, emptyMessageArray())), depTask1).output();
    given(willReturn(new Output(sstring2, emptyMessageArray())), depTask2).output();
    given(input = Input.fromResults(list(depTask1, depTask2)));
    given(input2 = Input.fromResults(list(depTask2, depTask1)));
    when(input).hash();
    thenReturned(not(input2.hash()));
  }

  @Test
  public void equal_inputs_have_equal_hashes() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = string("abc"));
    given(willReturn(new Output(sstring1, emptyMessageArray())), depTask1).output();
    given(willReturn(new Output(sstring1, emptyMessageArray())), depTask2).output();
    given(input = Input.fromResults(list(depTask1)));
    given(input2 = Input.fromResults(list(depTask2)));
    when(input).hash();
    thenReturned(input2.hash());
  }

  @Test
  public void input_with_no_values_has_hash_different_from_input_with_one_value() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = string("abc"));
    given(willReturn(new Output(sstring1, emptyMessageArray())), depTask1).output();
    given(input = Input.fromResults(list(depTask1)));
    given(input2 = Input.fromObjects(list()));
    when(input).hash();
    thenReturned(not(input2.hash()));
  }
}
