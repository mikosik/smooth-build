package org.smoothbuild.lang.value;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;
import org.smoothbuild.lang.type.TypesDb;

public class TestingValueFactory extends ValueFactory {
  public TestingValueFactory() {
    this(new TestingHashedDb());
  }

  public TestingValueFactory(HashedDb hashedDb) {
    this(hashedDb, new TypesDb(hashedDb));
  }

  public TestingValueFactory(HashedDb hashedDb, TypesDb typesDb) {
    this(new TestingRuntimeTypes(typesDb), new ValuesDb(hashedDb, typesDb));
  }

  public TestingValueFactory(TestingRuntimeTypes types, ValuesDb valuesDb) {
    super(types, valuesDb);
  }
}
