package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.testing.TestContext;

public class CatBCachingTest extends TestContext {
  @ParameterizedTest
  @MethodSource("factories")
  public void created_type_is_cached(Function<CatDb, CatB> factory) {
    assertThat(factory.apply(catDb()))
        .isSameInstanceAs(factory.apply(catDb()));
  }

  @ParameterizedTest
  @MethodSource("factories")
  public void read_type_is_cached(Function<CatDb, CatB> factory) {
    var hash = factory.apply(catDb()).hash();
    var catDb = catDbOther();
    assertThat(catDb.get(hash))
        .isSameInstanceAs(catDb.get(hash));
  }

  private static List<Function<CatDb, CatB>> factories() {
    return list(
        CatDb::blob,
        CatDb::bool,
        CatBCachingTest::funcT,
        CatDb::int_,
        CatDb::string,
        CatBCachingTest::tupleT,

        catDb -> catDb.call(catDb.int_()),
        catDb -> catDb.combine(catDb.tuple(list())),
        catDb -> catDb.combine(catDb.tuple(list(catDb.int_()))),
        catDb -> catDb.if_(catDb.int_()),
        catDb -> catDb.invoke(catDb.int_()),
        catDb -> catDb.map(catDb.array(catDb.int_())),
        catDb -> catDb.order(catDb.array(catDb.int_())),
        catDb -> catDb.paramRef(catDb.int_()),
        catDb -> catDb.select(catDb.int_()),

        catDb -> catDb.array(catDb.blob()),
        catDb -> catDb.array(catDb.bool()),
        catDb -> catDb.array(catDb.int_()),
        catDb -> catDb.array(catDb.string()),
        catDb -> catDb.array(tupleT(catDb)),
        catDb -> catDb.array(funcT(catDb)),

        catDb -> catDb.array(catDb.array(catDb.blob())),
        catDb -> catDb.array(catDb.array(catDb.bool())),
        catDb -> catDb.array(catDb.array(catDb.int_())),
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
