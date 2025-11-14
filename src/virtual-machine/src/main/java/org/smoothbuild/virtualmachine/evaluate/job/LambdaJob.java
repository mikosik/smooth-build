package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.failedOutput;
import static org.smoothbuild.common.schedule.Output.output;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.virtualmachine.VmConstants;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask.JobContext;

public final class LambdaJob extends Job {
  public LambdaJob(JobContext jobContext, BLambda lambda, List<Job> environment, Trace trace) {
    super(jobContext, lambda, environment, trace);
  }

  @Override
  public Promise<Maybe<BValue>> evaluate() {
    var inlineTask = (Task0<BValue>) () -> {
      var label = VmConstants.VM_LABEL.append(":inline");
      try {
        var inlined = (BValue) referenceInliner().inline(this);
        List<Log> logs = List.list();
        return output(inlined, report(label, trace(), logs));
      } catch (BytecodeException e) {
        return failedOutput(label, some(trace()), "Vm inline Task failed with exception:", e);
      }
    };
    return scheduler().submit(inlineTask);
  }
}
