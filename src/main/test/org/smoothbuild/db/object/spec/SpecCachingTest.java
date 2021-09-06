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
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
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
        ObjectDb::blobSpec,
        ObjectDb::boolSpec,
        SpecCachingTest::definedLambdaSpec,
        ObjectDb::intSpec,
        SpecCachingTest::nativeLambdaSpec,
        ObjectDb::nothingSpec,
        ObjectDb::strSpec,
        SpecCachingTest::recSpec,

        ObjectDb::callSpec,
        ObjectDb::constSpec,
        ObjectDb::eArraySpec,
        ObjectDb::fieldReadSpec,
        ObjectDb::nullSpec,
        ObjectDb::refSpec,

        (objectDb) -> objectDb.arraySpec(objectDb.blobSpec()),
        (objectDb) -> objectDb.arraySpec(objectDb.boolSpec()),
        (objectDb) -> objectDb.arraySpec(objectDb.intSpec()),
        (objectDb) -> objectDb.arraySpec(objectDb.nothingSpec()),
        (objectDb) -> objectDb.arraySpec(objectDb.strSpec()),
        (objectDb) -> objectDb.arraySpec(recSpec(objectDb)),
        (objectDb) -> objectDb.arraySpec(definedLambdaSpec(objectDb)),
        (objectDb) -> objectDb.arraySpec(nativeLambdaSpec(objectDb)),

        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.blobSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.boolSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.intSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.nothingSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(objectDb.strSpec())),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(recSpec(objectDb))),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(definedLambdaSpec(objectDb))),
        (objectDb) -> objectDb.arraySpec(objectDb.arraySpec(nativeLambdaSpec(objectDb)))
    );
  }

  private static RecSpec recSpec(ObjectDb objectDb) {
    return objectDb.recSpec(list(objectDb.strSpec(), objectDb.strSpec()));
  }

  private static DefinedLambdaSpec definedLambdaSpec(ObjectDb objectDb) {
    RecSpec parameters = objectDb.recSpec(list(objectDb.boolSpec(), objectDb.blobSpec()));
    return objectDb.definedLambdaSpec(objectDb.strSpec(), parameters);
  }

  private static NativeLambdaSpec nativeLambdaSpec(ObjectDb objectDb) {
    RecSpec parameters = objectDb.recSpec(list(objectDb.boolSpec(), objectDb.blobSpec()));
    return objectDb.nativeLambdaSpec(objectDb.strSpec(), parameters);
  }
}
