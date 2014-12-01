package org.smoothbuild.task.work;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.util.Empty;

import com.google.common.hash.HashCode;

public class TaskWorkerHashesTest {
  private static final CodeLocation CL = CodeLocation.codeLocation(2);

  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private TaskWorker<?> worker;
  private TaskWorker<?> worker2;

  private NativeFunction<SString> function;
  private NativeFunction<SString> function2;

  private final HashCode hash = Hash.integer(33);

  @Test
  public void constant_workers_with_different_value_have_different_hashes() throws Exception {
    given(worker = new ConstantWorker<>(STRING, objectsDb.string("abc"), CL));
    given(worker2 = new ConstantWorker<>(STRING, objectsDb.string("def"), CL));
    when(worker).hash();
    thenReturned(not(worker2.hash()));
  }

  @Test
  public void array_worker_and_constant_worker_have_different_hashes() throws Exception {
    given(worker = new ArrayWorker<>(STRING_ARRAY, CL));
    given(worker2 = new ConstantWorker<>(STRING, objectsDb.string("abc"), CL));
    when(worker).hash();
    thenReturned(not(worker2.hash()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void native_call_workers_with_different_functions_have_different_hashes() throws Exception {
    given(function =
        new NativeFunction<>(signature("fA"), mock(Invoker.class), false, Hash.integer(33)));
    given(function2 =
        new NativeFunction<>(signature("fB"), mock(Invoker.class), false, Hash.integer(34)));
    given(worker = new NativeCallWorker<>(function, asList("param"), false, CL));
    given(worker2 = new NativeCallWorker<>(function2, asList("param"), false, CL));
    when(worker).hash();
    thenReturned(not(worker2.hash()));
  }

  private static Signature<SString> signature(String name) {
    return new Signature<>(STRING, name(name), Empty.paramList());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void native_call_workers_with_same_functions_but_different_params_have_different_hashes()
      throws Exception {
    given(function = new NativeFunction<>(signature("fA"), mock(Invoker.class), false, hash));
    given(worker = new NativeCallWorker<>(function, asList("paramA"), false, CL));
    given(worker2 = new NativeCallWorker<>(function, asList("paramB"), false, CL));
    when(worker).hash();
    thenReturned(not(worker2.hash()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void native_call_worker_and_constant_worker_have_different_hashes() throws Exception {
    given(function = new NativeFunction<>(signature("fA"), mock(Invoker.class), false, hash));
    given(worker = new NativeCallWorker<>(function, asList("param"), false, CL));
    given(worker2 = new ConstantWorker<>(STRING, objectsDb.string("abc"), CL));
    when(worker).hash();
    thenReturned(not(worker2.hash()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void native_call_worker_and_array_worker_have_different_hashes() throws Exception {
    given(function = new NativeFunction<>(signature("fA"), mock(Invoker.class), false, hash));
    given(worker = new NativeCallWorker<>(function, asList("param"), false, CL));
    given(worker2 = new ArrayWorker<>(STRING_ARRAY, CL));
    when(worker).hash();
    thenReturned(not(worker2.hash()));
  }
}
