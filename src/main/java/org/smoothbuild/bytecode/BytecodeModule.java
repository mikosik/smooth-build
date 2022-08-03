package org.smoothbuild.bytecode;

import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;

import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.ObjDb;
import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.db.HashedDb;
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
  public ObjDb provideObjDb(HashedDb hashedDb, CatDb catDb) {
    return new ObjDbImpl(hashedDb, catDb);
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
