package org.smoothbuild.virtualmachine.bytecode.hashed;

import jakarta.inject.Inject;
import org.smoothbuild.common.init.Initializable;

public class HashedDbInitializer extends Initializable {
  private final HashedDb hashedDb;

  @Inject
  public HashedDbInitializer(HashedDb hashedDb) {
    super("HashedDb");
    this.hashedDb = hashedDb;
  }

  @Override
  protected void executeImpl() throws Exception {
    hashedDb.initialize();
  }
}
