package org.smoothbuild.common.init;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.concurrent.Promise.runWhenAllAvailable;
import static org.smoothbuild.common.init.Initializable.INITIALIZE_LABEL;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Output.schedulingOutput;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import java.util.Set;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.MutablePromise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.task.TaskExecutor;
import org.smoothbuild.common.tuple.Tuple0;

public class Initializer implements Task0<Tuple0> {
  private final Set<Initializable> initializables;
  private final TaskExecutor taskExecutor;

  @Inject
  public Initializer(Set<Initializable> initializables, TaskExecutor taskExecutor) {
    this.initializables = initializables;
    this.taskExecutor = taskExecutor;
  }

  @Override
  public Output<Tuple0> execute() {
    var initializablePromises = listOfAll(initializables).map(taskExecutor::submit);
    MutablePromise<Maybe<Tuple0>> promise = promise();
    runWhenAllAvailable(initializablePromises, () -> promise.accept(some(tuple())));
    var report = report(INITIALIZE_LABEL, new Trace(), EXECUTION, list());
    return schedulingOutput(promise, report);
  }
}
