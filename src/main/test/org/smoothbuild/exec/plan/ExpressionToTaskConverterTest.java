package org.smoothbuild.exec.plan;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.stream;
import static org.smoothbuild.lang.TestingLang.call;
import static org.smoothbuild.lang.TestingLang.function;
import static org.smoothbuild.lang.TestingLang.parameter;
import static org.smoothbuild.lang.TestingLang.parameterRef;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.TestingLang;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.Scope;

import com.google.common.collect.ImmutableMap;

public class ExpressionToTaskConverterTest extends TestingContext {
  @Test
  public void task_for_unused_arguments_are_not_created() {
    Expression blobLiteral = TestingLang.blob(0x22);
    Function function = function(BLOB, "myFunction", TestingLang.blob(0x33), parameter(BLOB, "p"));
    CallExpression call = call(11, BLOB, function, blobLiteral);
    Task rootTask = converter(function).visit(new Scope<>(Map.of()), call).getTask();

    assertThat(findTasks(rootTask, "0x22"))
        .hasSize(0);
    assertThat(findTasks(rootTask, "0x33"))
        .hasSize(1);
  }

  @Test
  public void only_one_task_is_created_for_argument_assigned_to_parameter_that_is_used_twice() {
    Function twoBlobsEater = function(
        BLOB, "twoBlobsEater", parameter(BLOB, "a"), parameter(BLOB, "b"));

    CallExpression twoBlobsEaterCall = call(
        BLOB, twoBlobsEater, parameterRef(BLOB, "param"), parameterRef(BLOB, "param"));
    Function myFunction = function(BLOB, "myFunction", twoBlobsEaterCall, parameter(BLOB, "param"));

    CallExpression myFunctionCall = call(BLOB, myFunction, TestingLang.blob(0x17));

    Task task = converter(myFunction, twoBlobsEater).visit(new Scope<>(Map.of()), myFunctionCall)
        .getTask();

    List<Task> found = findTasks(task, "0x17");
    assertThat(found)
        .hasSize(2);
    assertThat(found.get(0))
        .isSameInstanceAs(found.get(1));
  }

  private ExpressionToTaskConverter converter(Function... functions) {
    ImmutableMap<String, Referencable> referencables =
        stream(functions).collect(toImmutableMap(Defined::name, f -> f));
    var definitions = new Definitions(null, null, referencables);
    return new ExpressionToTaskConverter(definitions, objectFactory(), null, null);
  }

  private static List<Task> findTasks(Task task, String name) {
    ArrayList<Task> result = new ArrayList<>();
    findTasksRecursive(task, name, result);
    return result;
  }

  private static void findTasksRecursive(Task task, String name, ArrayList<Task> result) {
    if (task.name().equals(name)) {
      result.add(task);
    }
    task.dependencies().forEach(t -> findTasksRecursive(t.getTask(), name, result));
  }
}
