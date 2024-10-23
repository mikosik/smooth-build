package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.task.Output.output;

import java.util.function.Function;
import org.smoothbuild.common.log.base.Label;

public class Tasks {
  public static <R, A> Task1<R, A> map(Label label, Function<A, R> mapper) {
    return argument -> output(mapper.apply(argument), label, list());
  }
}
