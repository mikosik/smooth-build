package org.smoothbuild.task.base;

import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.STypes;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.testory.Closure;

import com.google.common.hash.HashCode;

public class TaskWorkerTest {
  private final SType<?> type = STypes.STRING;
  private final HashCode hash = Hash.string("");
  private final String name = "name";
  private final CodeLocation codeLocation = codeLocation(1);

  private TaskWorker<?> taskWorker;

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
    given(taskWorker = new MyTask<>(hash, type, name, false, codeLocation));
    when(taskWorker.resultType());
    thenReturned(type);
  }

  @Test
  public void name() throws Exception {
    given(taskWorker = new MyTask<>(hash, type, name, false, codeLocation));
    when(taskWorker.name());
    thenReturned(name);
  }

  @Test
  public void is_internal_return_true_when_true_passed_to_constructor() throws Exception {
    given(taskWorker = new MyTask<>(hash, type, name, true, codeLocation));
    when(taskWorker.isInternal());
    thenReturned(true);
  }

  @Test
  public void is_internal_return_false_when_false_passed_to_constructor() throws Exception {
    given(taskWorker = new MyTask<>(hash, type, name, false, codeLocation));
    when(taskWorker.isInternal());
    thenReturned(false);
  }

  @Test
  public void code_location() throws Exception {
    given(taskWorker = new MyTask<>(hash, type, name, false, codeLocation));
    when(taskWorker.codeLocation());
    thenReturned(codeLocation);
  }

  private static <T extends SValue> Closure $myTask(final SType<T> type, final String name,
      final boolean isInternal, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyTask<>(Hash.string(""), type, name, isInternal, codeLocation);
      }
    };
  }

  public static class MyTask<T extends SValue> extends TaskWorker<T> {
    public MyTask(HashCode hash, SType<T> type, String name, boolean isInternal,
        CodeLocation codeLocation) {
      super(hash, type, name, isInternal, true, codeLocation);
    }

    @Override
    public TaskOutput<T> execute(Iterable<? extends SValue> dependencies, NativeApiImpl nativeApi) {
      return null;
    }
  }
}
