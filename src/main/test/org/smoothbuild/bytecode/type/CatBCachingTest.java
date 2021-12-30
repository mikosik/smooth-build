package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

import com.google.common.truth.Truth;

public class CatBCachingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("type_creators")
  public void created_type_is_cached(Function<CatDb, CatB> typeCreator) {
    assertThat(typeCreator.apply(catDb()))
        .isSameInstanceAs(typeCreator.apply(catDb()));
  }

  @ParameterizedTest
  @MethodSource("type_creators")
  public void read_type_is_cached(Function<CatDb, CatB> typeCreator) {
    Hash hash = typeCreator.apply(catDb()).hash();
    CatDb catDb = catDbOther();
    Truth.assertThat(catDb.get(hash))
        .isSameInstanceAs(catDb.get(hash));
  }

  private static List<Function<CatDb, CatB>> type_creators() {
    return list(
        CatDb::blob,
        CatDb::bool,
        CatBCachingTest::funcT,
        CatDb::int_,
        CatDb::nothing,
        CatDb::string,
        CatBCachingTest::tupleT,

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

  private static TupleTB tupleT(CatDb catDb) {
    return catDb.tuple(list(catDb.string(), catDb.string()));
  }

  private static FuncTB funcT(CatDb catDb) {
    return catDb.func(catDb.string(), list(catDb.bool(), catDb.blob()));
  }
}
