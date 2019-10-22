package org.smoothbuild.lang.value;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;

public class TestingValueFactory extends ValueFactory {
  public TestingValueFactory() {
    this(new TestingHashedDb());
  }

  public TestingValueFactory(HashedDb hashedDb) {
    this(hashedDb, new ValuesDb(hashedDb));
  }

  public TestingValueFactory(HashedDb hashedDb, ValuesDb valuesDb) {
    this(new TestingRuntimeTypes(valuesDb), valuesDb);
  }

  public TestingValueFactory(TestingRuntimeTypes types, ValuesDb valuesDb) {
    super(types, valuesDb);
  }
}
