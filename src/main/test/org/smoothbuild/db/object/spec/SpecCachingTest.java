package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public class SpecCachingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("spec_creators")
  public void created_spec_is_cached(Function<ObjectDb, Spec> specCreator) {
    assertThat(specCreator.apply(objectDb()))
        .isSameInstanceAs(specCreator.apply(objectDb()));
  }

  @ParameterizedTest
  @MethodSource("spec_creators")
  public void read_spec_is_cached(Function<ObjectDb, Spec> specCreator) {
    Hash hash = specCreator.apply(objectDb()).hash();
    ObjectDb otherDb = objectDbOther();
    assertThat(otherDb.getSpec(hash))
        .isSameInstanceAs(otherDb.getSpec(hash));
  }

  private static List<Function<ObjectDb, Spec>> spec_creators() {
    return list(
        ObjectDb::anySpec,
        ObjectDb::blobSpec,
        ObjectDb::boolSpec,
        ObjectDb::nothingSpec,
        ObjectDb::stringSpec,
        SpecCachingTest::tupleSpec,
        (objectDb) -> objectDb.arraySpec(objectDb.anySpec()),
        (objectDb) -> objectDb.arraySpec(objectDb.blobSpec()),
        (objectDb) -> objectDb.arraySpec(objectDb.boolSpec()),
        (objectDb) -> objectDb.arraySpec(objectDb.nothingSpec()),
        (objectDb) -> objectDb.arraySpec(objectDb.stringSpec()),
        (objectDb) -> objectDb.arraySpec(tupleSpec(objectDb)),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.anySpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.blobSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.boolSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.nothingSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.stringSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(tupleSpec(objectDb)))
    );
  }

  private static TupleSpec tupleSpec(ObjectDb objectDb) {
    return objectDb.tupleSpec(list(objectDb.stringSpec(), objectDb.stringSpec()));
  }
}
