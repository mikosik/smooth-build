package org.smoothbuild.virtualmachine.wire;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryDb;

public class VirtualMachineWiring extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ExprDb provideExprDb(@BytecodeDb HashedDb hashedDb, CategoryDb categoryDb) {
    return new ExprDb(hashedDb, categoryDb);
  }

  @Provides
  @Singleton
  public CategoryDb provideCategoryDb(@BytecodeDb HashedDb hashedDb) {
    return new CategoryDb(hashedDb);
  }

  @Provides
  @Singleton
  @BytecodeDb
  private HashedDb provideHashedDb(@BytecodeDb Bucket bucket) {
    return new HashedDb(bucket);
  }
}
