package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask.JobContext;

public final class ReferenceJob extends SchedulingJob {
  private final BReference reference;

  public ReferenceJob(
      JobContext jobContext, BReference reference, List<Job> environment, Trace trace) {
    super(jobContext, reference, environment, trace);
    this.reference = reference;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    int index = reference.index().toJavaBigInteger().intValue();
    var referencedJob = environment().get(index);
    var jobEvaluationType = referencedJob.expr().evaluationType();
    if (jobEvaluationType.equals(reference.evaluationType())) {
      return referencedJob.evaluate();
    } else {
      throw new RuntimeException("environment(%d) evaluationType is %s but expected %s."
          .formatted(index, jobEvaluationType.q(), reference.evaluationType().q()));
    }
  }
}
