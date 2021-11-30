package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;

public class SpecHCachingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("type_creators")
  public void created_type_is_cached(Function<TypeDb, SpecH> typeCreator) {
    assertThat(typeCreator.apply(typeDb()))
        .isSameInstanceAs(typeCreator.apply(typeDb()));
  }

  @ParameterizedTest
  @MethodSource("type_creators")
  public void read_type_is_cached(Function<TypeDb, SpecH> typeCreator) {
    Hash hash = typeCreator.apply(typeDb()).hash();
    TypeDb typeDb = typeDbOther();
    assertThat(typeDb.get(hash))
        .isSameInstanceAs(typeDb.get(hash));
  }

  private static List<Function<TypeDb, SpecH>> type_creators() {
    return list(
        TypeDb::blob,
        TypeDb::bool,
        SpecHCachingTest::funcType,
        TypeDb::int_,
        TypeDb::nothing,
        TypeDb::string,
        SpecHCachingTest::tupleType,

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

  private static TupleTypeH tupleType(TypeDb typeDb) {
    return typeDb.tuple(list(typeDb.string(), typeDb.string()));
  }

  private static FuncTypeH funcType(TypeDb typeDb) {
    return typeDb.func(typeDb.string(), list(typeDb.bool(), typeDb.blob()));
  }
}
