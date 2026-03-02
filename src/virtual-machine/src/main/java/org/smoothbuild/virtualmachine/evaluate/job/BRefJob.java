package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BRefJob extends SchedulingJob {
  private final BRef ref;

  public BRefJob(JobContext jobContext, BRef ref, List<Job> environment, Trace trace) {
    super(jobContext, ref, environment, trace);
    this.ref = ref;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException, JobException {
    int index = ref.index().toJavaBigInteger().intValue();
    var referencedJob = environment().get(index);
    var jobEvaluationType = referencedJob.expr().evaluationType();
    if (jobEvaluationType.equals(ref.evaluationType())) {
      return referencedJob.evaluate();
    } else {
      throw new JobException("environment(%d) evaluationType is %s but expected %s."
          .formatted(index, jobEvaluationType.q(), ref.evaluationType().q()));
    }
  }
}
