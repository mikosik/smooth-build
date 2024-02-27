package org.smoothbuild.vm.bytecode;

import static org.smoothbuild.layout.ProjectSpaceLayout.HASHED_DB_PATH;
import static org.smoothbuild.layout.SmoothSpace.PROJECT;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.SubFileSystem;
import org.smoothbuild.layout.ForSpace;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.hashed.HashedDb;
import org.smoothbuild.vm.bytecode.type.CategoryDb;

public class BytecodeModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ExprDb provideExprDb(HashedDb hashedDb, CategoryDb categoryDb) {
    return new ExprDb(hashedDb, categoryDb);
  }

  @Provides
  @Singleton
  public CategoryDb provideCatDb(HashedDb hashedDb) {
    return new CategoryDb(hashedDb);
  }

  @Provides
  @Singleton
  private HashedDb provideHashedDb(@ForSpace(PROJECT) FileSystem fileSystem) {
    return new HashedDb(new SubFileSystem(fileSystem, HASHED_DB_PATH));
  }
}
