package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.testing.TestingContext;

public class CatHCachingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("type_creators")
  public void created_type_is_cached(Function<CatDb, CatH> typeCreator) {
    assertThat(typeCreator.apply(catDb()))
        .isSameInstanceAs(typeCreator.apply(catDb()));
  }

  @ParameterizedTest
  @MethodSource("type_creators")
  public void read_type_is_cached(Function<CatDb, CatH> typeCreator) {
    Hash hash = typeCreator.apply(catDb()).hash();
    CatDb catDb = catDbOther();
    assertThat(catDb.get(hash))
        .isSameInstanceAs(catDb.get(hash));
  }

  private static List<Function<CatDb, CatH>> type_creators() {
    return list(
        CatDb::blob,
        CatDb::bool,
        CatHCachingTest::funcT,
        CatDb::int_,
        CatDb::nothing,
        CatDb::string,
        CatHCachingTest::tupleT,

        catDb -> catDb.call(catDb.int_()),
        catDb -> catDb.order(catDb.int_()),
        catDb -> catDb.select(catDb.int_()),
        catDb -> catDb.ref(catDb.int_()),

        catDb -> catDb.array(catDb.blob()),
        catDb -> catDb.array(catDb.bool()),
        catDb -> catDb.array(catDb.int_()),
        catDb -> catDb.array(catDb.nothing()),
        catDb -> catDb.array(catDb.string()),
        catDb -> catDb.array(tupleT(catDb)),
        catDb -> catDb.array(funcT(catDb)),

        catDb -> catDb.array(catDb.array(catDb.blob())),
        catDb -> catDb.array(catDb.array(catDb.bool())),
        catDb -> catDb.array(catDb.array(catDb.int_())),
        catDb -> catDb.array(catDb.array(catDb.nothing())),
        catDb -> catDb.array(catDb.array(catDb.string())),
        catDb -> catDb.array(catDb.array(tupleT(catDb))),
        catDb -> catDb.array(catDb.array(funcT(catDb)))
    );
  }

  private static TupleTH tupleT(CatDb catDb) {
    return catDb.tuple(list(catDb.string(), catDb.string()));
  }

  private static FuncTH funcT(CatDb catDb) {
    return catDb.func(catDb.string(), list(catDb.bool(), catDb.blob()));
  }
}
