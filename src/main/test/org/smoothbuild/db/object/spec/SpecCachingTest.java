package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.testing.TestingContextImpl;

public class SpecCachingTest extends TestingContextImpl {
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
    assertThat(specDb.get(hash))
        .isSameInstanceAs(specDb.get(hash));
  }

  private static List<Function<SpecDb, Spec>> spec_creators() {
    return list(
        SpecDb::blob,
        SpecDb::bool,
        SpecCachingTest::lambdaSpec,
        SpecDb::int_,
        SpecDb::nothing,
        SpecDb::string,
        SpecCachingTest::recSpec,

        specDb -> specDb.call(specDb.int_()),
        specDb -> specDb.const_(specDb.int_()),
        specDb -> specDb.arrayExpr(specDb.int_()),
        specDb -> specDb.select(specDb.int_()),
        specDb -> specDb.ref(specDb.int_()),

        specDb -> specDb.array(specDb.blob()),
        specDb -> specDb.array(specDb.bool()),
        specDb -> specDb.array(specDb.int_()),
        specDb -> specDb.array(specDb.nothing()),
        specDb -> specDb.array(specDb.string()),
        specDb -> specDb.array(recSpec(specDb)),
        specDb -> specDb.array(lambdaSpec(specDb)),

        specDb -> specDb.array(specDb.array(specDb.blob())),
        specDb -> specDb.array(specDb.array(specDb.bool())),
        specDb -> specDb.array(specDb.array(specDb.int_())),
        specDb -> specDb.array(specDb.array(specDb.nothing())),
        specDb -> specDb.array(specDb.array(specDb.string())),
        specDb -> specDb.array(specDb.array(recSpec(specDb))),
        specDb -> specDb.array(specDb.array(lambdaSpec(specDb)))
    );
  }

  private static RecSpec recSpec(SpecDb specDb) {
    return specDb.rec(list(specDb.string(), specDb.string()));
  }

  private static LambdaSpec lambdaSpec(SpecDb specDb) {
    return specDb.function(specDb.string(), list(specDb.bool(), specDb.blob()));
  }
}
