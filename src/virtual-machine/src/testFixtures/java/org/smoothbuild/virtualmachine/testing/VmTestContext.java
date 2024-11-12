package org.smoothbuild.virtualmachine.testing;

import static com.google.common.base.Suppliers.memoize;
import static org.smoothbuild.common.bucket.base.SubBucket.subBucket;

import com.google.common.base.Supplier;
import java.io.IOException;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.testing.CommonTestContext;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.evaluate.compute.StepEvaluator;

public class VmTestContext extends CommonTestContext implements VmTestApi {
  private final Supplier<Bucket> projectBucket = memoize(this::synchronizedMemoryBucket);
  private final Supplier<StepEvaluator> stepEvaluator = memoize(this::newStepEvaluator);
  private final Supplier<BytecodeFactory> bytecodeFactory = memoize(this::newBytecodeFactory);
  private final Supplier<BExprDb> exprDb = memoize(this::newExprDb);
  private final Supplier<BKindDb> kindDb = memoize(this::newKindDb);
  private final Supplier<HashedDb> hashedDb = memoize(this::newHashDb);

  @Override
  public StepEvaluator stepEvaluator() {
    return stepEvaluator.get();
  }

  private StepEvaluator newStepEvaluator() {
    return new StepEvaluator(
        computationHashFactory(), this::container, computationCache(), scheduler(), bytecodeF());
  }

  public Bucket bytecodeBucket() {
    return subBucket(projectBucket(), BYTECODE_DB_SHORT_PATH);
  }

  @Override
  public Bucket projectBucket() {
    return projectBucket.get();
  }

  @Override
  public BytecodeFactory bytecodeF() {
    return bytecodeFactory.get();
  }

  private BytecodeFactory newBytecodeFactory() {
    return new BytecodeFactory(exprDb(), kindDb());
  }

  @Override
  public BExprDb exprDb() {
    return exprDb.get();
  }

  public BExprDb exprDbOther() {
    return new BExprDb(hashedDb(), kindDbOther());
  }

  private BExprDb newExprDb() {
    return new BExprDb(hashedDb(), kindDb());
  }

  @Override
  public BKindDb kindDb() {
    return kindDb.get();
  }

  public BKindDb kindDbOther() {
    return newKindDb();
  }

  private BKindDb newKindDb() {
    return new BKindDb(hashedDb());
  }

  public HashedDb hashedDb() {
    return hashedDb.get();
  }

  private HashedDb newHashDb() {
    var result = new HashedDb(bytecodeBucket());
    try {
      result.initialize();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  private Bucket newBytecodeBucket() {
    return new SynchronizedBucket(new MemoryBucket());
  }
}
