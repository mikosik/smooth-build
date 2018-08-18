package org.smoothbuild.task.base;

import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.task.exec.Container;

public class EvaluatorTest {
  private final String name = "name";
  private final Location location = location(Paths.get("script.smooth"), 1);
  private Evaluator evaluator;

  @Test
  public void null_name_is_forbidden() {
    when(() -> new MyEvaluator(null, true, list(), location));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_children_is_forbidden() {
    when(() -> new MyEvaluator(name, true, null, location));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when(() -> new MyEvaluator(name, true, list(), null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void name() throws Exception {
    given(evaluator = new MyEvaluator(name, false, list(), location));
    when(evaluator.name());
    thenReturned(name);
  }

  @Test
  public void is_internal_return_true_when_true_passed_to_constructor() throws Exception {
    given(evaluator = new MyEvaluator(name, true, list(), location));
    when(evaluator.isInternal());
    thenReturned(true);
  }

  @Test
  public void is_internal_return_false_when_false_passed_to_constructor() throws Exception {
    given(evaluator = new MyEvaluator(name, false, list(), location));
    when(evaluator.isInternal());
    thenReturned(false);
  }

  @Test
  public void code_location() throws Exception {
    given(evaluator = new MyEvaluator(name, false, list(), location));
    when(evaluator.location());
    thenReturned(location);
  }

  public static class MyEvaluator extends Evaluator {
    public MyEvaluator(String name, boolean isInternal, List<? extends Evaluator> children,
        Location location) {
      super(null, name, isInternal, true, children, location);
    }

    @Override
    public Output evaluate(Input input, Container container) {
      return null;
    }
  }
}
