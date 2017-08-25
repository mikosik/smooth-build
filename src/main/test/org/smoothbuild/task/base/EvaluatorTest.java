package org.smoothbuild.task.base;

import static org.smoothbuild.lang.message.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.collect.ImmutableList;

public class EvaluatorTest {
  private final String name = "name";
  private final CodeLocation codeLocation = codeLocation(1);

  private Evaluator evaluator;

  @Test
  public void null_name_is_forbidden() {
    when(() -> new MyEvaluator(null, true, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when(() -> new MyEvaluator(name, true, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void name() throws Exception {
    given(evaluator = new MyEvaluator(name, false, codeLocation));
    when(evaluator.name());
    thenReturned(name);
  }

  @Test
  public void is_internal_return_true_when_true_passed_to_constructor() throws Exception {
    given(evaluator = new MyEvaluator(name, true, codeLocation));
    when(evaluator.isInternal());
    thenReturned(true);
  }

  @Test
  public void is_internal_return_false_when_false_passed_to_constructor() throws Exception {
    given(evaluator = new MyEvaluator(name, false, codeLocation));
    when(evaluator.isInternal());
    thenReturned(false);
  }

  @Test
  public void code_location() throws Exception {
    given(evaluator = new MyEvaluator(name, false, codeLocation));
    when(evaluator.codeLocation());
    thenReturned(codeLocation);
  }

  public static class MyEvaluator extends Evaluator {
    public MyEvaluator(String name, boolean isInternal, CodeLocation codeLocation) {
      super(null, name, isInternal, true, codeLocation, ImmutableList.of());
    }

    public Output evaluate(Input input, ContainerImpl container) {
      return null;
    }
  }
}
