package org.smoothbuild.bytecode;

import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.ObjDb;
import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.TypeBF;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.db.HashedDb;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.space.ForSpace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class BytecodeModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ObjDb provideObjDb(HashedDb hashedDb, CatDb catDb, TypingB typing) {
    return new ObjDbImpl(hashedDb, catDb, typing);
  }

  @Provides
  public TypeBF provideTypeBF(CatDb catDb) {
    return catDb;
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
