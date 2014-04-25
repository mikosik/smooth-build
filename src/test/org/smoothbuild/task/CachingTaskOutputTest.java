package org.smoothbuild.task;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.ArrayExpr;
import org.smoothbuild.lang.expr.ConstantExpr;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.task.exec.Task;
import org.smoothbuild.task.exec.TaskGraph;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CachingTaskOutputTest {
  private static final CodeLocation CL = codeLocation(2);

  private ObjectsDb objectsDb;
  private TaskGraph taskGraph;

  private AtomicInteger counter;
  private CountingExpr expr1;
  private CountingExpr expr2;
  private ArrayExpr<SString> arrayExpr;
  private Task<?> task;

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
    given(expr1 = new CountingExpr(counter, Empty.exprList(), true));
    given(expr2 = new CountingExpr(counter, Empty.exprList(), true));
    given(arrayExpr = new ArrayExpr<>(STRING_ARRAY, ImmutableList.of(expr1, expr2), CL));
    given(task = taskGraph.createTasks(arrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(stringSArray("1", "1")));
  }

  @Test
  public void calculating_non_cacheable_expression_for_second_time_does_not_use_cached_output()
      throws Exception {
    given(counter = new AtomicInteger());
    given(expr1 = new CountingExpr(counter, Empty.exprList(), false));
    given(expr2 = new CountingExpr(counter, Empty.exprList(), false));
    given(arrayExpr = new ArrayExpr<>(STRING_ARRAY, ImmutableList.of(expr1, expr2), CL));
    given(task = taskGraph.createTasks(arrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(stringSArray("1", "2")));
  }

  @Test
  public void expression_of_same_type_but_different_dependencies_do_not_share_cached_results()
      throws Exception {
    given(counter = new AtomicInteger());
    given(expr1 = new CountingExpr(counter, ImmutableList.of(stringExpr("dep1")), true));
    given(expr2 = new CountingExpr(counter, ImmutableList.of(stringExpr("dep2")), true));
    given(arrayExpr = new ArrayExpr<>(STRING_ARRAY, ImmutableList.of(expr1, expr2), CL));
    given(task = taskGraph.createTasks(arrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(stringSArray("1", "2")));
  }

  private ConstantExpr<SString> stringExpr(String string) {
    return new ConstantExpr<>(STRING, objectsDb.string(string), CL);
  }

  private SArray<SString> stringSArray(String... strings) {
    ArrayBuilder<SString> builder = objectsDb.arrayBuilder(STRING_ARRAY);
    for (String string : strings) {
      builder.add(objectsDb.string(string));
    }
    return builder.build();
  }

  private static class CountingExpr extends Expr<SString> {
    private final AtomicInteger counter;
    private final boolean isCacheable;

    public CountingExpr(AtomicInteger counter, ImmutableList<? extends Expr<?>> dependencies,
        boolean isCacheable) {
      super(STRING, dependencies, CL);
      this.counter = counter;
      this.isCacheable = isCacheable;
    }

    @Override
    public TaskWorker<SString> createWorker() {
      return new CountingTaskWorker(counter, isCacheable);
    }
  }

  private static class CountingTaskWorker extends TaskWorker<SString> {
    private final AtomicInteger counter;

    public CountingTaskWorker(AtomicInteger counter, boolean isCacheable) {
      super(Hash.string("hash"), STRING, "counting", false, isCacheable, CodeLocation
          .codeLocation(2));
      this.counter = counter;
    }

    @Override
    public TaskOutput<SString> execute(Iterable<? extends SValue> input, NativeApiImpl nativeApi) {
      SString sstring = nativeApi.string(Integer.toString(counter.incrementAndGet()));
      return new TaskOutput<SString>(sstring);
    }
  }
}
