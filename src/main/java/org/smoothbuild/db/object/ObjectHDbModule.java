package org.smoothbuild.db.object;

import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.type.TypeFactoryH;
import org.smoothbuild.db.object.type.TypeHDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.space.ForSpace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ObjectHDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ObjectHDb provideObjectDb(HashedDb hashedDb, TypeHDb typeHDb, TypingH typing) {
    return new ObjectHDb(hashedDb, typeHDb, typing);
  }

  @Provides
  public TypeFactoryH provideTypeFactoryH(TypeHDb typeHDb) {
    return typeHDb;
  }

  @Provides
  @Singleton
  public TypeHDb provideObjTypeDb(HashedDb hashedDb) {
    return new TypeHDb(hashedDb);
  }

  @Provides
  @Singleton
  private HashedDb provideHashedDb(@ForSpace(PRJ) FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, OBJECT_DB_PATH, tempManager);
  }
}
