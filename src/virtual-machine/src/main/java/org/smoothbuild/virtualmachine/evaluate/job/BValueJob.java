package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.concurrent.Promise.promise;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BValueJob extends Job {
  public BValueJob(JobContext jobContext, BValue value, List<Job> environment, Trace trace) {
    super(jobContext, value, environment, trace);
  }

  @Override
  public Promise<Maybe<BValue>> evaluate() {
    try {
      var inlined = (BValue) refInliner().inline(this);
      return promise(some(inlined));
    } catch (BytecodeException | RefIndexOutOfBoundsException e) {
      throw new RuntimeException(e);
    }
  }
}
