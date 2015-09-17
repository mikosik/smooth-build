package org.smoothbuild.task.work;

import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;
import org.testory.Closure;

import com.google.common.hash.HashCode;

public class TaskWorkerTest {
  private final Type type = Types.STRING;
  private final HashCode hash = Hash.string("");
  private final String name = "name";
  private final CodeLocation codeLocation = codeLocation(1);

  private TaskWorker taskWorker;

  @Test
  public void null_type_is_forbidden() throws Exception {
    when($myTask(null, name, true, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    when($myTask(type, null, true, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when($myTask(type, name, true, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(taskWorker = new MyTaskWorker(hash, type, name, false, codeLocation));
    when(taskWorker.resultType());
    thenReturned(type);
  }

  @Test
  public void name() throws Exception {
    given(taskWorker = new MyTaskWorker(hash, type, name, false, codeLocation));
    when(taskWorker.name());
    thenReturned(name);
  }

  @Test
  public void is_internal_return_true_when_true_passed_to_constructor() throws Exception {
    given(taskWorker = new MyTaskWorker(hash, type, name, true, codeLocation));
    when(taskWorker.isInternal());
    thenReturned(true);
  }

  @Test
  public void is_internal_return_false_when_false_passed_to_constructor() throws Exception {
    given(taskWorker = new MyTaskWorker(hash, type, name, false, codeLocation));
    when(taskWorker.isInternal());
    thenReturned(false);
  }

  @Test
  public void code_location() throws Exception {
    given(taskWorker = new MyTaskWorker(hash, type, name, false, codeLocation));
    when(taskWorker.codeLocation());
    thenReturned(codeLocation);
  }

  private static <T extends Value> Closure $myTask(final Type type, final String name,
      final boolean isInternal, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyTaskWorker(Hash.string(""), type, name, isInternal, codeLocation);
      }
    };
  }

  public static class MyTaskWorker extends TaskWorker {
    public MyTaskWorker(HashCode hash, Type type, String name, boolean isInternal,
        CodeLocation codeLocation) {
      super(hash, type, name, isInternal, true, codeLocation);
    }

    @Override
    public TaskOutput execute(TaskInput input, ContainerImpl container) {
      return null;
    }
  }
}
