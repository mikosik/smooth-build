package org.smoothbuild.lang.function.nativ;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeNativeApi;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final NativeApiImpl nativeApi = new FakeNativeApi();
  private SString sstring;
  private TaskWorker<?> worker;

  private Signature<SString> signature = signature("name");
  @SuppressWarnings("unchecked")
  private final Invoker<SString> invoker = mock(Invoker.class);

  private NativeFunction<?> function;
  private ImmutableMap<String, SValue> args;
  private HashCode jarHash;
  private NativeFunction<SString> function2;

  @Test(expected = NullPointerException.class)
  public void null_signature_is_forbidden() throws Exception {
    new NativeFunction<>(Hash.integer(33), null, invoker, true);
  }

  @Test(expected = NullPointerException.class)
  public void null_invoker_is_forbidden() throws Exception {
    new NativeFunction<>(Hash.integer(33), signature, null, true);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void invoke_is_forwarded_to_invoker() throws Exception {
    given(function = new NativeFunction<>(Hash.integer(33), signature, invoker, true));
    given(args = mock(ImmutableMap.class));
    when(function).invoke(nativeApi, args);
    thenCalled(invoker).invoke(nativeApi, args);
  }

  @Test
  public void task_worker_uses_invoker_for_calculating_result() throws Exception {
    given(function = new NativeFunction<>(Hash.integer(33), signature, invoker, true));
    given(sstring = objectsDb.string("result"));
    given(willReturn(sstring), invoker).invoke(nativeApi, Empty.stringValueMap());
    given(worker = function.createWorker(Empty.stringExprMap(), codeLocation(1)));
    when(worker).execute(TaskInput.fromTaskReturnValues(Empty.taskList()), nativeApi);
    thenReturned(new TaskOutput<>(sstring));
  }

  @Test
  public void functions_with_same_name_in_the_same_jar_have_same_hash() throws Exception {
    given(jarHash = Hash.integer(33));
    given(function = new NativeFunction<>(jarHash, signature, invoker, false));
    given(function2 = new NativeFunction<>(jarHash, signature, invoker, false));
    when(function).hash();
    thenReturned(function2.hash());
  }

  @Test
  public void functions_with_different_names_in_the_same_jar_have_different_hash() throws Exception {
    given(jarHash = Hash.integer(33));
    given(function = new NativeFunction<>(jarHash, signature("name"), invoker, false));
    given(function2 = new NativeFunction<>(jarHash, signature("name2"), invoker, false));
    when(function).hash();
    thenReturned(not(function2.hash()));
  }

  @Test
  public void functions_with_same_names_in_different_jars_have_different_hash() throws Exception {
    given(signature = signature("name"));
    given(function = new NativeFunction<>(Hash.integer(33), signature, invoker, false));
    given(function2 = new NativeFunction<>(Hash.integer(44), signature, invoker, false));
    when(function).hash();
    thenReturned(not(function2.hash()));
  }

  private static Signature<SString> signature(String name) {
    return new Signature<>(STRING, name(name), Empty.paramList());
  }
}
