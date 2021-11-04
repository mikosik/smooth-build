package org.smoothbuild.exec.plan;

import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.job.Job;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator.Handler;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.TestingLocation;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JobCreatorTest extends TestingContext {
  @Test
  public void lazy_task_is_created_for_parameter() {
    var functionBody = blobExpression(0x33);
    var function = functionExpression(BLOB, "myFunction", functionBody, parameter(BLOB, "p"));

    var argument = new MyExpression();
    var call = callExpression(11, BLOB, referenceExpression(function), argument);

    taskCreator(oneLazyCallAllowed(), function)
        .eagerJobFor(new Scope<>(Map.of()), call);
  }

  @Test
  public void only_one_lazy_task_is_created_for_argument_assigned_to_parameter_that_is_used_twice() {
    Function twoBlobsEater = functionExpression(
        BLOB, "twoBlobsEater", parameter(BLOB, "a"), parameter(BLOB, "b"));

    CallExpression twoBlobsEaterCall = callExpression(BLOB, referenceExpression(twoBlobsEater),
        parameterRefExpression(BLOB, "param"), parameterRefExpression(BLOB, "param"));
    Function myFunction = functionExpression(
        BLOB, "myFunction", twoBlobsEaterCall, parameter(BLOB, "param"));

    CallExpression myFunctionCall = callExpression(BLOB, referenceExpression(myFunction),
        new MyExpression());

    taskCreator(oneLazyCallAllowed(), myFunction, twoBlobsEater)
        .eagerJobFor(new Scope<>(Map.of()), myFunctionCall);
  }

  private static  ImmutableMap<Class<?>, Handler<?>> oneLazyCallAllowed() {
    AtomicInteger counter = new AtomicInteger(1);
    return ImmutableMap.of(MyExpression.class, new Handler<>(
        (scope, o) -> {
          if (counter.getAndDecrement() != 1) {
            throw new RuntimeException();
          }
          return new Job() {
            @Override
            public Type type() {
              return STRING;
            }

            @Override
            public ImmutableList<Job> dependencies() {
              return list();
            }

            @Override
            public Promise<Val> schedule(Worker worker) {
              return null;
            }

            @Override
            public String name() {
              return "name";
            }

            @Override
            public Location location() {
              return TestingLocation.loc();
            }
          };
        },
        (scope, o) -> {
          throw new RuntimeException();
        }));
  }

  private JobCreator taskCreator(
      Map<Class<?>, Handler<?>> additionalHandlers, Function... functions) {
    var definitions = definitions(functions);
    return new JobCreator(definitions, new TypeSToTypeOConverter(objectFactory()), null,
        typeFactoryS(), typingS(), additionalHandlers);
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
