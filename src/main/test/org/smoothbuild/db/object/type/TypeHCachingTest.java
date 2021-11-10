package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;

public class TypeHCachingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("type_creators")
  public void created_type_is_cached(Function<TypeHDb, TypeH> typeCreator) {
    assertThat(typeCreator.apply(objTypeDb()))
        .isSameInstanceAs(typeCreator.apply(objTypeDb()));
  }

  @ParameterizedTest
  @MethodSource("type_creators")
  public void read_type_is_cached(Function<TypeHDb, TypeH> typeCreator) {
    Hash hash = typeCreator.apply(objTypeDb()).hash();
    TypeHDb typeHDb = objTypeDbOther();
    assertThat(typeHDb.get(hash))
        .isSameInstanceAs(typeHDb.get(hash));
  }

  private static List<Function<TypeHDb, TypeH>> type_creators() {
    return list(
        TypeHDb::blob,
        TypeHDb::bool,
        TypeHCachingTest::functionType,
        TypeHDb::int_,
        TypeHDb::nothing,
        TypeHDb::string,
        TypeHCachingTest::tupleType,

        objTypeDb -> objTypeDb.call(objTypeDb.int_()),
        objTypeDb -> objTypeDb.const_(objTypeDb.int_()),
        objTypeDb -> objTypeDb.const_(objTypeDb.int_()),
        objTypeDb -> objTypeDb.order(objTypeDb.int_()),
        objTypeDb -> objTypeDb.select(objTypeDb.int_()),
        objTypeDb -> objTypeDb.ref(objTypeDb.int_()),

        objTypeDb -> objTypeDb.array(objTypeDb.blob()),
        objTypeDb -> objTypeDb.array(objTypeDb.bool()),
        objTypeDb -> objTypeDb.array(objTypeDb.int_()),
        objTypeDb -> objTypeDb.array(objTypeDb.nothing()),
        objTypeDb -> objTypeDb.array(objTypeDb.string()),
        objTypeDb -> objTypeDb.array(tupleType(objTypeDb)),
        objTypeDb -> objTypeDb.array(functionType(objTypeDb)),

        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.blob())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.bool())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.int_())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.nothing())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.string())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(tupleType(objTypeDb))),
        objTypeDb -> objTypeDb.array(objTypeDb.array(functionType(objTypeDb)))
    );
  }

  private static TupleTypeH tupleType(TypeHDb typeHDb) {
    return typeHDb.tuple(list(typeHDb.string(), typeHDb.string()));
  }

  private static FunctionTypeH functionType(TypeHDb typeHDb) {
    return typeHDb.function(typeHDb.string(), list(typeHDb.bool(), typeHDb.blob()));
  }
}