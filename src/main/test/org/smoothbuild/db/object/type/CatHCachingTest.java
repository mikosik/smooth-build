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
        CatHCachingTest::funcType,
        CatDb::int_,
        CatDb::nothing,
        CatDb::string,
        CatHCachingTest::tupleType,

        objTypeDb -> objTypeDb.call(objTypeDb.int_()),
        objTypeDb -> objTypeDb.order(objTypeDb.int_()),
        objTypeDb -> objTypeDb.select(objTypeDb.int_()),
        objTypeDb -> objTypeDb.ref(objTypeDb.int_()),

        objTypeDb -> objTypeDb.array(objTypeDb.blob()),
        objTypeDb -> objTypeDb.array(objTypeDb.bool()),
        objTypeDb -> objTypeDb.array(objTypeDb.int_()),
        objTypeDb -> objTypeDb.array(objTypeDb.nothing()),
        objTypeDb -> objTypeDb.array(objTypeDb.string()),
        objTypeDb -> objTypeDb.array(tupleType(objTypeDb)),
        objTypeDb -> objTypeDb.array(funcType(objTypeDb)),

        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.blob())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.bool())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.int_())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.nothing())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.string())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(tupleType(objTypeDb))),
        objTypeDb -> objTypeDb.array(objTypeDb.array(funcType(objTypeDb)))
    );
  }

  private static TupleTH tupleType(CatDb catDb) {
    return catDb.tuple(list(catDb.string(), catDb.string()));
  }

  private static FuncTH funcType(CatDb catDb) {
    return catDb.func(catDb.string(), list(catDb.bool(), catDb.blob()));
  }
}
