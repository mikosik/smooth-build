package org.smoothbuild.exec.plan;

import static org.smoothbuild.lang.TestingLang.call;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.parameterRef;
import static org.smoothbuild.lang.TestingLang.reference;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Maps.toMap;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.plan.TaskCreator.Handler;
import org.smoothbuild.lang.TestingLang;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.Scope;

import com.google.common.collect.ImmutableMap;

public class TaskCreatorTest extends TestingContext {
  @Test
  public void lazy_task_is_created_for_parameter() {
    var functionBody = TestingLang.blob(0x33);
    var function = function(BLOB, "myFunction", functionBody, parameter(BLOB, "p"));

    var argument = new MyExpression();
    var call = call(11, BLOB, reference(function), argument);

    taskCreator(oneLazyCallAllowed(), function)
        .eagerTaskFor(new Scope<>(Map.of()), call);
  }

  @Test
  public void only_one_lazy_task_is_created_for_argument_assigned_to_parameter_that_is_used_twice() {
    Function twoBlobsEater = function(
        BLOB, "twoBlobsEater", parameter(BLOB, "a"), parameter(BLOB, "b"));

    CallExpression twoBlobsEaterCall = call(
        BLOB, reference(twoBlobsEater), parameterRef(BLOB, "param"), parameterRef(BLOB, "param"));
    Function myFunction = function(BLOB, "myFunction", twoBlobsEaterCall, parameter(BLOB, "param"));

    CallExpression myFunctionCall = call(BLOB, reference(myFunction), new MyExpression());

    taskCreator(oneLazyCallAllowed(), myFunction, twoBlobsEater)
        .eagerTaskFor(new Scope<>(Map.of()), myFunctionCall);
  }

  private static  ImmutableMap<Class<?>, Handler<?>> oneLazyCallAllowed() {
    AtomicInteger counter = new AtomicInteger(1);
    return ImmutableMap.of(MyExpression.class, new Handler<>(
        (scope, o) -> {
          if (counter.getAndDecrement() != 1) {
            throw new RuntimeException();
          }
          return null;
        },
        (scope, o) -> {
          throw new RuntimeException();
        }));
  }

  private TaskCreator taskCreator(
      Map<Class<?>, Handler<?>> additionalHandlers, Function... functions) {
    var definitions = definitions(functions);
    return new TaskCreator(definitions, new TypeToSpecConverter(objectFactory()), null,
        additionalHandlers);
  }

  private Definitions definitions(Function... functions) {
    return new Definitions(
        ImmutableMap.of(), null, toMap(list(functions), Defined::name, f -> f));
  }

  private static class MyExpression implements Expression {
    @Override
    public Type type() {
      return STRING;
    }

    @Override
    public Location location() {
      return loc();
    }
  }
}
