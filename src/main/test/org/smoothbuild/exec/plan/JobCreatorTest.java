package org.smoothbuild.exec.plan;

import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.exec.job.Job;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator.Handler;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.FunctionS;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.TestingLocation;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.CallS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.collect.NamedList;
import org.smoothbuild.util.concurrent.Promise;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class JobCreatorTest extends TestingContext {
  @Test
  public void lazy_task_is_created_for_parameter() {
    var functionBody = blobS(0x33);
    var function = functionS(BLOB, "myFunction", functionBody, param(BLOB, "p"));

    var argument = new MyExpression();
    var call = callS(11, BLOB, refS(function), argument);

    taskCreator(oneLazyCallAllowed(), function)
        .eagerJobFor(new Scope<>(NamedList.empty()), call);
  }

  @Test
  public void only_one_lazy_task_is_created_for_argument_assigned_to_parameter_that_is_used_twice() {
    FunctionS twoBlobsEater = functionS(
        BLOB, "twoBlobsEater", param(BLOB, "a"), param(BLOB, "b"));

    CallS twoBlobsEaterCall = callS(BLOB, refS(twoBlobsEater),
        paramRefS(BLOB, "param"), paramRefS(BLOB, "param"));
    FunctionS myFunction = functionS(
        BLOB, "myFunction", twoBlobsEaterCall, param(BLOB, "param"));

    CallS myFunctionCall = callS(BLOB, refS(myFunction), new MyExpression());

    taskCreator(oneLazyCallAllowed(), myFunction, twoBlobsEater)
        .eagerJobFor(new Scope<>(NamedList.empty()), myFunctionCall);
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
            public TypeS type() {
              return STRING;
            }

            @Override
            public ImmutableList<Job> dependencies() {
              return list();
            }

            @Override
            public Promise<ValueH> schedule(Worker worker) {
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
      Map<Class<?>, Handler<?>> additionalHandlers, FunctionS... functions) {
    var definitions = definitions(functions);
    return new JobCreator(definitions, new TypeSToTypeOConverter(objFactory()), null,
        typeFactoryS(), typingS(), additionalHandlers);
  }

  private Definitions definitions(FunctionS... functions) {
    return new Definitions(ImmutableMap.of(), null, namedList(list(functions)));
  }

  private static class MyExpression implements ExprS {
    @Override
    public TypeS type() {
      return STRING;
    }

    @Override
    public Location location() {
      return loc();
    }
  }
}
