package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.failedOutput;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BInlineJob extends Job {
  public BInlineJob(JobContext jobContext, BExpr lambda, List<Job> environment, Trace trace) {
    super(jobContext, lambda, environment, trace);
  }

  @Override
  public Promise<Maybe<BValue>> evaluate() {
    var inlineTask = (Task0<BValue>) () -> {
      var label = VM_LABEL.append(":inline");
      try {
        var inlined = (BValue) refInliner().inline(this);
        return output(inlined, report(label, trace(), list()));
      } catch (BytecodeException | RefIndexOutOfBoundsException e) {
        return failedOutput(label, some(trace()), "Vm inline Task failed with exception:", e);
      }
    };
    return scheduler().submit(inlineTask);
  }
}
