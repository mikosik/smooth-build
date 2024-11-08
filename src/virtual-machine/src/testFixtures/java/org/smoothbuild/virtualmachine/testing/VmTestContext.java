package org.smoothbuild.virtualmachine.testing;

import static com.google.common.base.Suppliers.memoize;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.SubBucket.subBucket;

import com.google.common.base.Supplier;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.virtualmachine.evaluate.compute.StepEvaluator;

public class VmTestContext extends BytecodeTestContext implements VmTestApi {
  private final Supplier<Bucket> projectBucket = memoize(this::synchronizedMemoryBucket);
  private final Supplier<StepEvaluator> stepEvaluator = memoize(this::newStepEvaluator);

  @Override
  public StepEvaluator stepEvaluator() {
    return stepEvaluator.get();
  }

  private StepEvaluator newStepEvaluator() {
    return new StepEvaluator(
        computationHashFactory(), this::container, computationCache(), scheduler(), bytecodeF());
  }

  @Override
  public Bucket bytecodeBucket() {
    // TODO hardcoded
    return subBucket(projectBucket(), path(".smooth/bytecode"));
  }

  @Override
  public Bucket projectBucket() {
    return projectBucket.get();
  }
}
