package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.function.base.FakeSignature.testSignature;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class DefinedFunctionTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  Signature signature = testSignature();
  LocatedNode root = mock(LocatedNode.class);
  CodeLocation codeLocation = new FakeCodeLocation();

  DefinedFunction definedFunction = new DefinedFunction(signature, root);

  @Test(expected = NullPointerException.class)
  public void nullRootIsForbidden() {
    new DefinedFunction(signature, null);
  }

  @Test
  public void generateTaskWithEmptyDependency() throws Exception {
    LocatedTask task = mock(LocatedTask.class);
    when(root.generateTask(taskGenerator)).thenReturn(task);

    Task actual = definedFunction.generateTask(taskGenerator, Empty.stringTaskResultMap(),
        codeLocation);

    assertThat(actual).isSameAs(task);
  }

  @Test(expected = IllegalArgumentException.class)
  public void generateTaskThrowsExceptionWhenDependenciesAreNotEmpty() throws Exception {
    definedFunction.generateTask(taskGenerator, ImmutableMap.of("name", mock(Result.class)),
        codeLocation);
  }

  @Test
  public void generateTaskForwardsCallToRootNode() throws Exception {
    LocatedTask task = mock(LocatedTask.class);
    when(root.generateTask(taskGenerator)).thenReturn(task);

    Task actual = definedFunction.generateTask(taskGenerator);

    assertThat(actual).isSameAs(task);
  }
}
