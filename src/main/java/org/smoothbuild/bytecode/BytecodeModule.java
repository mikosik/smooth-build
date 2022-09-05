package org.smoothbuild.bytecode;

import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;

import javax.inject.Singleton;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.install.TempManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class BytecodeModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public BytecodeDb provideBytecodeDb(HashedDb hashedDb, CatDb catDb) {
    return new BytecodeDb(hashedDb, catDb);
  }

  @Provides
  @Singleton
  public CatDb provideCatDb(HashedDb hashedDb) {
    return new CatDb(hashedDb);
  }

  @Provides
  @Singleton
  private HashedDb provideHashedDb(@ForSpace(PRJ) FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, HASHED_DB_PATH, tempManager);
  }
}
