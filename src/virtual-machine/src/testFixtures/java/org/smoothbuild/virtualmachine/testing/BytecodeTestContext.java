package org.smoothbuild.virtualmachine.testing;

import static com.google.common.base.Suppliers.memoize;

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

public class BytecodeTestContext extends CommonTestContext implements BytecodeTestApi {
  private final Supplier<BytecodeFactory> bytecodeFactory = memoize(this::newBytecodeFactory);
  private final Supplier<BExprDb> exprDb = memoize(this::newExprDb);
  private final Supplier<BKindDb> kindDb = memoize(this::newKindDb);
  private final Supplier<HashedDb> hashedDb = memoize(this::newHashDb);
  private final Supplier<Bucket> bytecodeBucket = memoize(this::newBytecodeBucket);

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
    var result = hashedDb.get();
    try {
      result.initialize();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  private HashedDb newHashDb() {
    return new HashedDb(bytecodeBucket());
  }

  public Bucket bytecodeBucket() {
    return bytecodeBucket.get();
  }

  private Bucket newBytecodeBucket() {
    return new SynchronizedBucket(new MemoryBucket());
  }
}
