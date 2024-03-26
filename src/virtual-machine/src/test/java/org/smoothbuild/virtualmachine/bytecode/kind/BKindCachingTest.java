package org.smoothbuild.virtualmachine.bytecode.kind;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BKindCachingTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("factories")
  public void created_type_is_cached(Function1<BKindDb, BKind, BytecodeException> factory)
      throws Exception {
    assertThat(factory.apply(kindDb())).isSameInstanceAs(factory.apply(kindDb()));
  }

  @ParameterizedTest
  @MethodSource("factories")
  public void read_type_is_cached(Function1<BKindDb, BKind, BytecodeException> factory)
      throws Exception {
    var hash = factory.apply(kindDb()).hash();
    var kindDb = kindDbOther();
    assertThat(kindDb.get(hash)).isSameInstanceAs(kindDb.get(hash));
  }

  private static java.util.List<Function1<BKindDb, BKind, BytecodeException>> factories() {
    return list(
        BKindDb::blob,
        BKindDb::bool,
        BKindCachingTest::funcT,
        BKindDb::int_,
        BKindDb::string,
        BKindCachingTest::tupleT,
        kindDb -> kindDb.call(kindDb.int_()),
        kindDb -> kindDb.combine(kindDb.tuple()),
        kindDb -> kindDb.combine(kindDb.tuple(kindDb.int_())),
        kindDb -> kindDb.lambda(kindDb.funcT(list(), kindDb.int_())),
        kindDb -> kindDb.if_(kindDb.int_()),
        kindDb -> kindDb.mapFunc(kindDb.int_(), kindDb.string()),
        kindDb -> kindDb.order(kindDb.array(kindDb.int_())),
        kindDb -> kindDb.pick(kindDb.int_()),
        kindDb -> kindDb.reference(kindDb.int_()),
        kindDb -> kindDb.select(kindDb.int_()),
        kindDb -> kindDb.array(kindDb.blob()),
        kindDb -> kindDb.array(kindDb.bool()),
        kindDb -> kindDb.array(kindDb.int_()),
        kindDb -> kindDb.array(kindDb.string()),
        kindDb -> kindDb.array(tupleT(kindDb)),
        kindDb -> kindDb.array(funcT(kindDb)),
        kindDb -> kindDb.array(kindDb.array(kindDb.blob())),
        kindDb -> kindDb.array(kindDb.array(kindDb.bool())),
        kindDb -> kindDb.array(kindDb.array(kindDb.int_())),
        kindDb -> kindDb.array(kindDb.array(kindDb.string())),
        kindDb -> kindDb.array(kindDb.array(tupleT(kindDb))),
        kindDb -> kindDb.array(kindDb.array(funcT(kindDb))));
  }

  private static BTupleType tupleT(BKindDb kindDb) throws BytecodeException {
    return kindDb.tuple(kindDb.string(), kindDb.string());
  }

  private static BFuncType funcT(BKindDb kindDb) throws BytecodeException {
    return kindDb.funcT(list(kindDb.bool(), kindDb.blob()), kindDb.string());
  }
}
