package org.smoothbuild.task;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValueFactory;
import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.lang.expr.ArrayExpr;
import org.smoothbuild.lang.expr.CallExpr;
import org.smoothbuild.lang.expr.ConstantExpr;
import org.smoothbuild.lang.expr.ConvertExpr;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.expr.InvalidExpr;
import org.smoothbuild.lang.expr.err.CannotCreateTaskWorkerFromInvalidNodeError;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.Task;
import org.smoothbuild.task.exec.TaskGraph;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ExpressionExecutionTest {
  private final String string = "abc";
  private final CodeLocation location = CodeLocation.codeLocation(33);
  private ObjectsDb objectsDb;
  private SString sstring;
  private Expr<SString> stringExpr;
  private Expr<?> expression;
  private Expr<?> arrayExpr;
  private TaskGraph taskGraph;
  private Task<?> task;
  private ConvertExpr<SString, SString> converted;
  private CallExpr<?> callExpr;
  private Function<?> function;
  private Signature<SString> signature;
  private Invoker<SString> invoker;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestExecutorModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
    taskGraph = injector.getInstance(TaskGraph.class);
  }

  @Test
  public void executes_string_literal_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpr = new ConstantExpr<>(STRING, sstring, location));
    given(task = taskGraph.createTasks(stringExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(sstring));
  }

  @Test
  public void executes_invalid_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(expression = new InvalidExpr<>(STRING, location));
    when(taskGraph).createTasks(expression);
    thenThrown(new CannotCreateTaskWorkerFromInvalidNodeError());
  }

  @Test
  public void executes_convert_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpr = new ConstantExpr<>(STRING, sstring, location));
    given(converted = new ConvertExpr<>(stringExpr, new DoubleStringConverter(), location));
    given(task = taskGraph.createTasks(converted));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(objectsDb.string(string + string)));
  }

  @Test
  public void executes_empty_array_expression() throws Exception {
    given(arrayExpr = new ArrayExpr<>(STRING_ARRAY, ImmutableList.<Expr<SString>> of(), location));
    given(task = taskGraph.createTasks(arrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(array()));
  }

  @Test
  public void executes_array_expression() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpr = new ConstantExpr<>(STRING, sstring, location));
    given(arrayExpr = new ArrayExpr<>(STRING_ARRAY, ImmutableList.of(stringExpr), location));
    given(task = taskGraph.createTasks(arrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(array(sstring)));
  }

  @Test
  public void executes_call_expression_using_defined_function() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpr = new ConstantExpr<>(STRING, sstring, location));
    given(signature = new Signature<>(STRING, name("name"), Empty.paramList()));
    given(function = new DefinedFunction<>(signature, stringExpr));
    given(callExpr = new CallExpr<>(function, location, Empty.stringExprMap()));
    given(task = taskGraph.createTasks(callExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(sstring));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void executes_call_expression_using_native_function() throws Exception {
    given(sstring = objectsDb.string(string));
    given(stringExpr = new ConstantExpr<>(STRING, sstring, location));
    given(signature = new Signature<>(STRING, name("name"), Empty.paramList()));
    given(invoker = mock(Invoker.class));
    given(willReturn(sstring), invoker).invoke(any(NativeApi.class), any(Map.class));
    given(function = new NativeFunction<>(signature, invoker, true));
    given(callExpr = new CallExpr<>(function, location, Empty.stringExprMap()));
    given(task = taskGraph.createTasks(callExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(sstring));
  }

  private SArray<SString> array(SString... sstrings) {
    ArrayBuilder<SString> arrayBuilder = objectsDb.arrayBuilder(STRING_ARRAY);
    for (SString sstring : sstrings) {
      arrayBuilder.add(sstring);
    }
    return arrayBuilder.build();
  }

  private static class DoubleStringConverter extends Converter<SString, SString> {
    public DoubleStringConverter() {
      super(STRING, STRING);
    }

    @Override
    public SString convert(SValueFactory valueFactory, SString sstring) {
      String value = sstring.value();
      return valueFactory.string(value + value);
    }
  }
}
