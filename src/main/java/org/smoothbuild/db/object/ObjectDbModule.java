package org.smoothbuild.db.object;

import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.spec.SpecDb;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.impl.TypeFactoryImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ObjectDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ObjectDb provideObjectDb(HashedDb hashedDb, SpecDb specDb) {
    return new ObjectDb(hashedDb, specDb);
  }

  @Provides
  @Singleton
  public TypeFactory provideTypeFactory() {
    return new TypeFactoryImpl();
  }

  @Provides
  @Singleton
  public SpecDb provideSpecDb(HashedDb hashedDb) {
    return new SpecDb(hashedDb);
  }

  @Provides
  @Singleton
  private HashedDb provideHashedDb(@ForSpace(PRJ) FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, OBJECT_DB_PATH, tempManager);
  }
}
