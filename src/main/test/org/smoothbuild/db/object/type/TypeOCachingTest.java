package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.val.LambdaTypeO;
import org.smoothbuild.db.object.type.val.TupleTypeO;
import org.smoothbuild.testing.TestingContext;

public class TypeOCachingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("type_creators")
  public void created_type_is_cached(Function<ObjTypeDb, TypeO> typeCreator) {
    assertThat(typeCreator.apply(objTypeDb()))
        .isSameInstanceAs(typeCreator.apply(objTypeDb()));
  }

  @ParameterizedTest
  @MethodSource("type_creators")
  public void read_type_is_cached(Function<ObjTypeDb, TypeO> typeCreator) {
    Hash hash = typeCreator.apply(objTypeDb()).hash();
    ObjTypeDb objTypeDb = objTypeDbOther();
    assertThat(objTypeDb.get(hash))
        .isSameInstanceAs(objTypeDb.get(hash));
  }

  private static List<Function<ObjTypeDb, TypeO>> type_creators() {
    return list(
        ObjTypeDb::blob,
        ObjTypeDb::bool,
        TypeOCachingTest::lambdaType,
        ObjTypeDb::int_,
        ObjTypeDb::nothing,
        ObjTypeDb::string,
        TypeOCachingTest::tupleType,

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
        objTypeDb -> objTypeDb.array(lambdaType(objTypeDb)),

        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.blob())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.bool())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.int_())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.nothing())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(objTypeDb.string())),
        objTypeDb -> objTypeDb.array(objTypeDb.array(tupleType(objTypeDb))),
        objTypeDb -> objTypeDb.array(objTypeDb.array(lambdaType(objTypeDb)))
    );
  }

  private static TupleTypeO tupleType(ObjTypeDb objTypeDb) {
    return objTypeDb.tuple(list(objTypeDb.string(), objTypeDb.string()));
  }

  private static LambdaTypeO lambdaType(ObjTypeDb objTypeDb) {
    return objTypeDb.function(objTypeDb.string(), list(objTypeDb.bool(), objTypeDb.blob()));
  }
}
