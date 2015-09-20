package org.smoothbuild.task;

import static com.google.inject.util.Modules.override;
import static java.util.Arrays.asList;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.util.SmoothJar;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ValueExpression;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Algorithm;
import org.smoothbuild.task.base.Computer;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.ContainerImpl;
import org.smoothbuild.task.exec.TaskGraph;
import org.smoothbuild.util.Empty;

import com.google.common.hash.HashCode;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;

public class CachingTaskOutputTest {
  private static final CodeLocation CL = codeLocation(2);

  private ObjectsDb objectsDb;
  private TaskGraph taskGraph;
  private TaskGraph taskGraph2;

  private AtomicInteger counter;
  private CountingExpression expression1;
  private CountingExpression expression2;
  private ArrayExpression arrayExpression;
  private Task task;
  private Task task2;

  private Module module;
  private Injector injector;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestExecutorModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
    taskGraph = injector.getInstance(TaskGraph.class);
  }

  @Test
  public void calculating_cacheable_expression_for_second_time_uses_cached_output()
      throws Exception {
    given(counter = new AtomicInteger());
    given(expression1 = new CountingExpression(counter, Empty.expressionList(), true));
    given(expression2 = new CountingExpression(counter, Empty.expressionList(), true));
    given(arrayExpression = new ArrayExpression(STRING_ARRAY, asList(expression1, expression2),
        CL));
    given(task = taskGraph.createTasks(arrayExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new Output(stringArray("1", "1")));
  }

  @Test
  public void calculating_non_cacheable_expression_for_second_time_does_not_use_cached_output()
      throws Exception {
    given(counter = new AtomicInteger());
    given(expression1 = new CountingExpression(counter, Empty.expressionList(), false));
    given(expression2 = new CountingExpression(counter, Empty.expressionList(), false));
    given(arrayExpression = new ArrayExpression(STRING_ARRAY, asList(expression1, expression2),
        CL));
    given(task = taskGraph.createTasks(arrayExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new Output(stringArray("1", "2")));
  }

  @Test
  public void expression_of_same_type_but_different_dependencies_do_not_share_cached_results()
      throws Exception {
    given(counter = new AtomicInteger());
    given(expression1 = new CountingExpression(counter, asList(stringExpression("dep1")), true));
    given(expression2 = new CountingExpression(counter, asList(stringExpression("dep2")), true));
    given(arrayExpression = new ArrayExpression(STRING_ARRAY, asList(expression1, expression2),
        CL));
    given(task = taskGraph.createTasks(arrayExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new Output(stringArray("1", "2")));
  }

  @Test
  public void smooth_jar_hash_is_used_for_calculating_hash_for_task_outputs_db() throws Exception {
    given(module = override(new TestExecutorModule()).with(new GrowingSmoothJarHashModule()));
    given(injector = Guice.createInjector(module));
    given(taskGraph = injector.getInstance(TaskGraph.class));
    given(taskGraph2 = injector.getInstance(TaskGraph.class));
    given(objectsDb = injector.getInstance(ObjectsDb.class));
    given(counter = new AtomicInteger());
    given(expression1 = new CountingExpression(counter, Empty.expressionList(), true));
    given(expression2 = new CountingExpression(counter, Empty.expressionList(), true));
    given(task = taskGraph.createTasks(expression1));
    given(task2 = taskGraph2.createTasks(expression2));
    given(taskGraph).executeAll();
    when(taskGraph2).executeAll();
    thenEqual(task.output(), new Output(objectsDb.string("1")));
    thenEqual(task2.output(), new Output(objectsDb.string("2")));
  }

  private static class GrowingSmoothJarHashModule extends AbstractModule {
    private int counter = 0;

    @Override
    protected void configure() {}

    @Provides
    @SmoothJar
    public HashCode provideSmoothJarHash() {
      return HashCode.fromInt(counter++);
    }
  }

  private ValueExpression stringExpression(String string) {
    return new ValueExpression(objectsDb.string(string), CL);
  }

  private Array<SString> stringArray(String... strings) {
    ArrayBuilder<SString> builder = objectsDb.arrayBuilder(SString.class);
    for (String string : strings) {
      builder.add(objectsDb.string(string));
    }
    return builder.build();
  }

  private static class CountingExpression extends Expression {
    private final AtomicInteger counter;
    private final boolean isCacheable;

    public CountingExpression(AtomicInteger counter, List<? extends Expression> dependencies,
        boolean isCacheable) {
      super(STRING, dependencies, CL);
      this.counter = counter;
      this.isCacheable = isCacheable;
    }

    @Override
    public Computer createComputer() {
      return new MyCountingComputer(counter, isCacheable);
    }
  }

  private static class MyCountingComputer extends Computer {
    public MyCountingComputer(AtomicInteger counter, boolean isCacheable) {
      super(new MyCountingAlgorithm(counter), "counting", false, isCacheable, CodeLocation
          .codeLocation(2));
    }
  }

  private static class MyCountingAlgorithm implements Algorithm {
    private final AtomicInteger counter;

    public MyCountingAlgorithm(AtomicInteger counter) {
      this.counter = counter;
    }

    @Override
    public Output execute(Input input, ContainerImpl container) {
      SString sstring = container.string(Integer.toString(counter.incrementAndGet()));
      return new Output(sstring);
    }

    @Override
    public HashCode hash() {
      return Hash.string("hash");
    }

    @Override
    public Type resultType() {
      return STRING;
    }
  }
}
