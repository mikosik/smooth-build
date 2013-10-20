package org.smoothbuild.function.nativ;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.function.base.FakeSignature.testSignature;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeFunctionTest {
  Sandbox sandbox = mock(Sandbox.class);
  String name = "functionName";
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  Signature signature = testSignature("functionName");
  Invoker invoker = mock(Invoker.class);

  NativeFunction function = new NativeFunction(signature, invoker);

  @Test(expected = NullPointerException.class)
  public void nullInvokerIsForbidden() throws Exception {
    new NativeFunction(signature, null);
  }

  @Test
  public void invokeIsForwardedToInvoker() throws Exception {
    @SuppressWarnings("unchecked")
    ImmutableMap<String, Object> args = mock(ImmutableMap.class);
    function.invoke(sandbox, args);
    verify(invoker).invoke(sandbox, args);
  }

  @Test
  public void generateTaskReturnsTaskWithNoResultCalculated() throws Exception {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);

    Task task = function.generateTask(taskGenerator, Empty.stringHashMap(), codeLocation);
    assertThat(task.isResultCalculated()).isFalse();
  }

  @Test
  public void generatedTaskHasPassedArgsAsDependencies() throws Exception {
    ImmutableMap<String, HashCode> args = Empty.stringHashMap();
    TaskGenerator taskGenerator = mock(TaskGenerator.class);

    Task task = function.generateTask(taskGenerator, args, codeLocation);
    assertThat(task.dependencies()).isSameAs(args.values());
  }

  @Test
  public void generatedTaskUsesInvokerForCalculatingResult() throws Exception {
    String result = "result";

    // given
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(result);
    TaskGenerator taskGenerator = mock(TaskGenerator.class);

    // when
    Task task = function.generateTask(taskGenerator, Empty.stringHashMap(), codeLocation);
    task.execute(sandbox, hashedTasks());

    // then
    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(result);
  }
}
