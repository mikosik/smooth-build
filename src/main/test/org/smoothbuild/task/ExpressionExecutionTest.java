package org.smoothbuild.task;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.expr.Expressions.callExpression;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.nativ.NativeFunctionFactory.nativeFunction;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.expr.ArrayExpression;
import org.smoothbuild.lang.expr.ConstantExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.InvalidExpression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGraph;
import org.smoothbuild.util.Empty;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ExpressionExecutionTest {
  private final String string = "abc";
  private final CodeLocation location = CodeLocation.codeLocation(33);
  private ObjectsDb objectsDb;
  private SString sstring;
  private Expression stringExpression;
  private Expression expression;
  private Expression arrayExpression;
  private TaskGraph taskGraph;
  private Task task;
  private Expression callExpression;
  private Function function;
  private Signature signature;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestExecutorModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
    taskGraph = injector.getInstance(TaskGraph.class);
  }

  @Test
  public void executes_string_literal_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpression = new ConstantExpression(sstring, location));
    given(task = taskGraph.createTasks(stringExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new Output(sstring));
  }

  @Test
  public void executes_invalid_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(expression = new InvalidExpression(STRING, location));
    when(taskGraph).createTasks(expression);
    thenThrown(RuntimeException.class);
  }

  @Test
  public void executes_empty_array_expression() throws Exception {
    given(arrayExpression = new ArrayExpression(STRING_ARRAY, Arrays.<Expression> asList(),
        location));
    given(task = taskGraph.createTasks(arrayExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new Output(array()));
  }

  @Test
  public void executes_array_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpression = new ConstantExpression(sstring, location));
    given(arrayExpression = new ArrayExpression(STRING_ARRAY, asList(stringExpression), location));
    given(task = taskGraph.createTasks(arrayExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new Output(array(sstring)));
  }

  @Test
  public void executes_call_expression_using_defined_function() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpression = new ConstantExpression(sstring, location));
    given(signature = new Signature(STRING, name("name"), Empty.paramList()));
    given(function = new DefinedFunction(signature, stringExpression));
    given(callExpression = callExpression(function, false, location, Empty.expressionList()));
    given(task = taskGraph.createTasks(callExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new Output(sstring));
  }

  @Test
  public void executes_native_function_that_returns_its_argument() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(function = nativeFunction(SmoothModule.class.getMethods()[0], Hash.integer(33)));
    given(stringExpression = new ConstantExpression(sstring, codeLocation(2)));
    given(callExpression = callExpression(function, false, location, asList(stringExpression)));
    given(task = taskGraph.createTasks(callExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new Output(sstring));
  }

  public static class SmoothModule {
    @SmoothFunction
    public static SString func(Container container, @Name("string") SString string) {
      return string;
    }
  }

  @Test
  public void execution_fails_when_native_function_throws_runtime_exception() throws Exception {
    given(sstring = objectsDb.string("abc"));
    given(function = nativeFunction(SmoothModule2.class.getMethods()[0], Hash.integer(33)));
    given(stringExpression = new ConstantExpression(sstring, codeLocation(2)));
    given(callExpression = callExpression(function, false, location, asList(stringExpression)));
    given(task = taskGraph.createTasks(callExpression));
    when(taskGraph).executeAll();
    thenEqual(task.output().hasResult(), false);
  }

  public static class SmoothModule2 {
    @SmoothFunction
    public static SString func(Container container, @Name("string") SString string) {
      throw new RuntimeException();
    }
  }

  private Array<SString> array(SString... sstrings) {
    ArrayBuilder<SString> arrayBuilder = objectsDb.arrayBuilder(SString.class);
    for (SString sstring : sstrings) {
      arrayBuilder.add(sstring);
    }
    return arrayBuilder.build();
  }
}
