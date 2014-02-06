package org.smoothbuild.task.exec;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.function.def.CachingNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.StringNode;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.message.FakeCodeLocation;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TaskGeneratorTest {
  SString string = mock(SString.class);
  Result result;
  Node node;

  TaskGenerator taskGenerator;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new IntegrationTestModule());
    taskGenerator = injector.getInstance(TaskGenerator.class);
  }

  @Test
  public void generated_task_when_executed_returns_proper_result() {
    given(node = new StringNode(string, new FakeCodeLocation()));
    given(result = taskGenerator.generateTask(node));
    when(result.value());
    thenReturned(string);
  }

  @Test
  public void generating_task_twice_for_the_same_node_returns_the_same_task_container() {
    given(node = new CachingNode(new StringNode(string, new FakeCodeLocation())));
    given(result = taskGenerator.generateTask(node));
    when(taskGenerator.generateTask(node));
    thenReturned(result);
  }
}
