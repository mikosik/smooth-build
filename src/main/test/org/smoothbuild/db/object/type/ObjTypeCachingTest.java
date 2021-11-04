package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ObjType;
import org.smoothbuild.db.object.type.val.LambdaOType;
import org.smoothbuild.db.object.type.val.TupleOType;
import org.smoothbuild.testing.TestingContext;

public class ObjTypeCachingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("type_creators")
  public void created_type_is_cached(Function<ObjTypeDb, ObjType> typeCreator) {
    assertThat(typeCreator.apply(objTypeDb()))
        .isSameInstanceAs(typeCreator.apply(objTypeDb()));
  }

  @ParameterizedTest
  @MethodSource("type_creators")
  public void read_type_is_cached(Function<ObjTypeDb, ObjType> typeCreator) {
    Hash hash = typeCreator.apply(objTypeDb()).hash();
    ObjTypeDb objTypeDb = objTypeDbOther();
    assertThat(objTypeDb.get(hash))
        .isSameInstanceAs(objTypeDb.get(hash));
  }

  private static List<Function<ObjTypeDb, ObjType>> type_creators() {
    return list(
        ObjTypeDb::blob,
        ObjTypeDb::bool,
        ObjTypeCachingTest::lambdaType,
        ObjTypeDb::int_,
        ObjTypeDb::nothing,
        ObjTypeDb::string,
        ObjTypeCachingTest::tupleType,

        objTypeDb -> objTypeDb.call(objTypeDb.int_()),
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

  private static TupleOType tupleType(ObjTypeDb objTypeDb) {
    return objTypeDb.tuple(list(objTypeDb.string(), objTypeDb.string()));
  }

  private static LambdaOType lambdaType(ObjTypeDb objTypeDb) {
    return objTypeDb.function(objTypeDb.string(), list(objTypeDb.bool(), objTypeDb.blob()));
  }
}
