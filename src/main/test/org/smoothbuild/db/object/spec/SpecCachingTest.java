package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.val.TupleSpec;
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
        ObjectDb::blobS,
        ObjectDb::boolS,
        ObjectDb::intS,
        ObjectDb::nothingS,
        ObjectDb::strS,
        SpecCachingTest::tupleSpec,
        ObjectDb::callS,
        ObjectDb::constS,
        ObjectDb::eArrayS,
        ObjectDb::fieldReadS,

        (objectDb) -> objectDb.arrayS(objectDb.blobS()),
        (objectDb) -> objectDb.arrayS(objectDb.boolS()),
        (objectDb) -> objectDb.arrayS(objectDb.intS()),
        (objectDb) -> objectDb.arrayS(objectDb.nothingS()),
        (objectDb) -> objectDb.arrayS(objectDb.strS()),
        (objectDb) -> objectDb.arrayS(tupleSpec(objectDb)),

        (objectDb) -> objectDb.arrayS(objectDb.arrayS(objectDb.blobS())),
        (objectDb) -> objectDb.arrayS(objectDb.arrayS(objectDb.boolS())),
        (objectDb) -> objectDb.arrayS(objectDb.arrayS(objectDb.intS())),
        (objectDb) -> objectDb.arrayS(objectDb.arrayS(objectDb.nothingS())),
        (objectDb) -> objectDb.arrayS(objectDb.arrayS(objectDb.strS())),
        (objectDb) -> objectDb.arrayS(objectDb.arrayS(tupleSpec(objectDb)))
    );
  }

  private static TupleSpec tupleSpec(ObjectDb objectDb) {
    return objectDb.tupleS(list(objectDb.strS(), objectDb.strS()));
  }
}
