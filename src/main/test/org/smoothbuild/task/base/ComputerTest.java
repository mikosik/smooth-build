package org.smoothbuild.task.base;

import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.ContainerImpl;
import org.testory.Closure;

public class ComputerTest {
  private final String name = "name";
  private final CodeLocation codeLocation = codeLocation(1);

  private Computer computer;

  @Test
  public void null_name_is_forbidden() {
    when($myTask(null, true, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when($myTask(name, true, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void name() throws Exception {
    given(computer = new MyComputer(name, false, codeLocation));
    when(computer.name());
    thenReturned(name);
  }

  @Test
  public void is_internal_return_true_when_true_passed_to_constructor() throws Exception {
    given(computer = new MyComputer(name, true, codeLocation));
    when(computer.isInternal());
    thenReturned(true);
  }

  @Test
  public void is_internal_return_false_when_false_passed_to_constructor() throws Exception {
    given(computer = new MyComputer(name, false, codeLocation));
    when(computer.isInternal());
    thenReturned(false);
  }

  @Test
  public void code_location() throws Exception {
    given(computer = new MyComputer(name, false, codeLocation));
    when(computer.codeLocation());
    thenReturned(codeLocation);
  }

  private static <T extends Value> Closure $myTask(final String name, final boolean isInternal,
      final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyComputer(name, isInternal, codeLocation);
      }
    };
  }

  public static class MyComputer extends Computer {
    public MyComputer(String name, boolean isInternal, CodeLocation codeLocation) {
      super(null, name, isInternal, true, codeLocation);
    }

    @Override
    public Output execute(Input input, ContainerImpl container) {
      return null;
    }
  }
}
