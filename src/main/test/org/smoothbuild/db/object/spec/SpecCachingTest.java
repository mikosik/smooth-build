package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.SpecDb;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.testing.TestingContext;

public class SpecCachingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("spec_creators")
  public void created_spec_is_cached(Function<SpecDb, Spec> specCreator) {
    assertThat(specCreator.apply(specDb()))
        .isSameInstanceAs(specCreator.apply(specDb()));
  }

  @ParameterizedTest
  @MethodSource("spec_creators")
  public void read_spec_is_cached(Function<SpecDb, Spec> specCreator) {
    Hash hash = specCreator.apply(specDb()).hash();
    SpecDb specDb = specDbOther();
    assertThat(specDb.getSpec(hash))
        .isSameInstanceAs(specDb.getSpec(hash));
  }

  private static List<Function<SpecDb, Spec>> spec_creators() {
    return list(
        SpecDb::blobSpec,
        SpecDb::boolSpec,
        SpecCachingTest::lambdaSpec,
        SpecDb::intSpec,
        SpecDb::nothingSpec,
        SpecDb::strSpec,
        SpecCachingTest::recSpec,

        specDb -> specDb.callSpec(specDb.intSpec()),
        specDb -> specDb.constSpec(specDb.intSpec()),
        specDb -> specDb.arrayExprSpec(specDb.intSpec()),
        specDb -> specDb.selectSpec(specDb.intSpec()),
        SpecDb::nullSpec,
        specDb -> specDb.refSpec(specDb.intSpec()),

        specDb -> specDb.arraySpec(specDb.blobSpec()),
        specDb -> specDb.arraySpec(specDb.boolSpec()),
        specDb -> specDb.arraySpec(specDb.intSpec()),
        specDb -> specDb.arraySpec(specDb.nothingSpec()),
        specDb -> specDb.arraySpec(specDb.strSpec()),
        specDb -> specDb.arraySpec(recSpec(specDb)),
        specDb -> specDb.arraySpec(lambdaSpec(specDb)),

        specDb -> specDb.arraySpec(specDb.arraySpec(specDb.blobSpec())),
        specDb -> specDb.arraySpec(specDb.arraySpec(specDb.boolSpec())),
        specDb -> specDb.arraySpec(specDb.arraySpec(specDb.intSpec())),
        specDb -> specDb.arraySpec(specDb.arraySpec(specDb.nothingSpec())),
        specDb -> specDb.arraySpec(specDb.arraySpec(specDb.strSpec())),
        specDb -> specDb.arraySpec(specDb.arraySpec(recSpec(specDb))),
        specDb -> specDb.arraySpec(specDb.arraySpec(lambdaSpec(specDb)))
    );
  }

  private static RecSpec recSpec(SpecDb specDb) {
    return specDb.recSpec(list(specDb.strSpec(), specDb.strSpec()));
  }

  private static LambdaSpec lambdaSpec(SpecDb specDb) {
    return specDb.lambdaSpec(specDb.strSpec(), list(specDb.boolSpec(), specDb.blobSpec()));
  }
}
