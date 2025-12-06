package org.smoothbuild.virtualmachine.evaluate.job;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BParamRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BParamRefJob extends SchedulingJob {
  private final BParamRef paramRef;

  public BParamRefJob(
      JobContext jobContext, BParamRef paramRef, List<Job> environment, Trace trace) {
    super(jobContext, paramRef, environment, trace);
    this.paramRef = paramRef;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException, JobException {
    int index = paramRef.index().toJavaBigInteger().intValue();
    var referencedJob = environment().get(index);
    var jobEvaluationType = referencedJob.expr().evaluationType();
    if (jobEvaluationType.equals(paramRef.evaluationType())) {
      return referencedJob.evaluate();
    } else {
      throw new JobException("environment(%d) evaluationType is %s but expected %s."
          .formatted(index, jobEvaluationType.q(), paramRef.evaluationType().q()));
    }
  }
}
