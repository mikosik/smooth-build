package org.smoothbuild.task;

import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.createNativeFunction;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.ConstantExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.InvalidExpression;
import org.smoothbuild.lang.expr.err.CannotCreateTaskWorkerFromInvalidExprError;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.TaskGraph;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ExpressionExecutionTest {
  private final String string = "abc";
  private final CodeLocation location = CodeLocation.codeLocation(33);
  private ObjectsDb objectsDb;
  private SString sstring;
  private Expression<SString> stringExpression;
  private Expression<?> expression;
  private Expression<?> arrayExpression;
  private TaskGraph taskGraph;
  private Task<?> task;
  private CallExpression<?> callExpression;
  private Function<?> function;
  private Signature<SString> signature;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestExecutorModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
    taskGraph = injector.getInstance(TaskGraph.class);
  }

  @Test
  public void executes_string_literal_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpression = new ConstantExpression<>(STRING, sstring, location));
    given(task = taskGraph.createTasks(stringExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(sstring));
  }

  @Test
  public void executes_invalid_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(expression = new InvalidExpression<>(STRING, location));
    when(taskGraph).createTasks(expression);
    thenThrown(new CannotCreateTaskWorkerFromInvalidExprError());
  }

  @Test
  public void executes_empty_array_expression() throws Exception {
    given(arrayExpression = new ArrayExpression<>(STRING_ARRAY, ImmutableList.<Expression<SString>>of(), location));
    given(task = taskGraph.createTasks(arrayExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(array()));
  }

  @Test
  public void executes_array_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpression = new ConstantExpression<>(STRING, sstring, location));
    given(arrayExpression = new ArrayExpression<>(STRING_ARRAY, ImmutableList.of(stringExpression), location));
    given(task = taskGraph.createTasks(arrayExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(array(sstring)));
  }

  @Test
  public void executes_call_expression_using_defined_function() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpression = new ConstantExpression<>(STRING, sstring, location));
    given(signature = new Signature<>(STRING, name("name"), Empty.paramList()));
    given(function = new DefinedFunction<>(signature, stringExpression));
    given(callExpression = new CallExpression<>(function, false, location, Empty.stringExprMap()));
    given(task = taskGraph.createTasks(callExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(sstring));
  }

  @Test
  public void executes_native_function_that_returns_its_argument() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(function = createNativeFunction(Hash.integer(33), SmoothModule.class.getMethods()[0]));
    given(stringExpression = new ConstantExpression<>(STRING, sstring, codeLocation(2)));
    given(callExpression = new CallExpression<>(function, false, location, ImmutableMap.of("param",
        stringExpression)));
    given(task = taskGraph.createTasks(callExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(sstring));
  }

  public static class SmoothModule {
    public interface Parameters {
      SString param();
    }

    @SmoothFunction
    public static SString func(NativeApi nativeApi, Parameters params) {
      return params.param();
    }
  }

  @Test
  public void execution_fails_when_native_function_throws_runtime_exception() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(function = createNativeFunction(Hash.integer(33), SmoothModule2.class.getMethods()[0]));
    given(stringExpression = new ConstantExpression<>(STRING, sstring, codeLocation(2)));
    given(callExpression = new CallExpression<>(function, false, location, ImmutableMap.of("param",
        stringExpression)));
    given(task = taskGraph.createTasks(callExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output().hasReturnValue(), false);
  }

  public static class SmoothModule2 {
    public interface Parameters {
      SString param();
    }

    @SmoothFunction
    public static SString func(NativeApi nativeApi, Parameters params) {
      throw new RuntimeException();
    }
  }

  private Array<SString> array(SString... sstrings) {
    ArrayBuilder<SString> arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY);
    for (SString sstring : sstrings) {
      arrayBuilder.add(sstring);
    }
    return arrayBuilder.build();
  }
}
