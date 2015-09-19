package org.smoothbuild.task.base;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.util.Empty;

public class InputTest {
  private Task depTask1;
  private Task depTask2;
  private Input input;
  private Input input2;
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private SString sstring1;
  private SString sstring2;

  @Test
  public void input_takes_values_from_dependency_tasks() {
    given(depTask1 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(willReturn(new Output(sstring1)), depTask1).output();
    given(input = Input.fromResults(asList(depTask1)));
    when(input).values();
    thenReturned(contains(sstring1));
  }

  @Test
  public void different_inputs_have_different_hashes() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    given(willReturn(new Output(sstring1)), depTask1).output();
    given(willReturn(new Output(sstring2)), depTask2).output();
    given(input = Input.fromResults(asList(depTask1)));
    given(input2 = Input.fromResults(asList(depTask2)));
    when(input).hash();
    thenReturned(not(input2.hash()));
  }

  @Test
  public void inputs_with_same_values_but_in_different_order_have_different_hashes()
      throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    given(willReturn(new Output(sstring1)), depTask1).output();
    given(willReturn(new Output(sstring2)), depTask2).output();
    given(input = Input.fromResults(asList(depTask1, depTask2)));
    given(input2 = Input.fromResults(asList(depTask2, depTask1)));
    when(input).hash();
    thenReturned(not(input2.hash()));
  }

  @Test
  public void equal_inputs_have_equal_hashes() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(willReturn(new Output(sstring1)), depTask1).output();
    given(willReturn(new Output(sstring1)), depTask2).output();
    given(input = Input.fromResults(asList(depTask1)));
    given(input2 = Input.fromResults(asList(depTask2)));
    when(input).hash();
    thenReturned(input2.hash());
  }

  @Test
  public void input_with_no_values_has_hash_different_from_input_with_one_value() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(willReturn(new Output(sstring1)), depTask1).output();
    given(input = Input.fromResults(asList(depTask1)));
    given(input2 = Input.fromResults(Empty.taskList()));
    when(input).hash();
    thenReturned(not(input2.hash()));
  }
}
