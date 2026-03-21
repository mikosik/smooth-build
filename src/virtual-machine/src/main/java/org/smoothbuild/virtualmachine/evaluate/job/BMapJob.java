package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.successOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;

public final class BMapJob extends SchedulingJob {
  private final BMap map;

  public BMapJob(JobContext jobContext, BMap map, List<Job> environment, Trace trace) {
    super(jobContext, map, environment, trace);
    this.map = map;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var arrayArg = map.array();
    var schedulingTask = newMapSchedulingTask();
    var arrayPromise = evaluate(arrayArg);
    return scheduler().submit(schedulingTask, arrayPromise);
  }

  private Task1<BValue, BValue> newMapSchedulingTask() {
    return (arrayValue) -> {
      try {
        var array = ((BArray) arrayValue);
        var mapperArg = map.mapper();
        var calls = array.elements(BValue.class).map(e -> call(mapperArg, list(e)));
        var mappingLambdaResultType = ((BLambdaType) mapperArg.evaluationType()).result();
        var arrayType = bytecodeFactory().arrayType(mappingLambdaResultType);
        var mappedArray = bytecodeFactory().constructArray(arrayType, calls);
        return successOutput(evaluate(mappedArray), executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Label executeLabel() {
    return scheduleLabel("execute");
  }
}
