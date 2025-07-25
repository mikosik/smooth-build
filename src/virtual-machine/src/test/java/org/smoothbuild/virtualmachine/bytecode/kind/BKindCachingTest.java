package org.smoothbuild.virtualmachine.bytecode.kind;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BKindCachingTest extends VmTestContext {
  @ParameterizedTest
  @MethodSource("factories")
  public void created_type_is_cached(Function1<BKindDb, BKind, BytecodeException> factory)
      throws Exception {
    assertThat(factory.apply(provide().kindDb()))
        .isSameInstanceAs(factory.apply(provide().kindDb()));
  }

  @ParameterizedTest
  @MethodSource("factories")
  public void read_type_is_cached(Function1<BKindDb, BKind, BytecodeException> factory)
      throws Exception {
    var hash = factory.apply(provide().kindDb()).hash();
    var kindDb = kindDbOther();
    assertThat(kindDb.get(hash)).isSameInstanceAs(kindDb.get(hash));
  }

  private static Stream<Function1<BKindDb, BKind, BytecodeException>> factories() {
    return Stream.of(
        BKindDb::blob,
        BKindDb::bool,
        BKindCachingTest::lambdaType,
        BKindDb::int_,
        BKindDb::string,
        BKindCachingTest::choiceType,
        BKindCachingTest::tupleType,
        kindDb -> kindDb.call(kindDb.int_()),
        kindDb -> kindDb.combine(kindDb.tuple()),
        kindDb -> kindDb.combine(kindDb.tuple(kindDb.int_())),
        kindDb -> kindDb.lambda(list(), kindDb.int_()),
        kindDb -> kindDb.if_(kindDb.int_()),
        kindDb -> kindDb.map(kindDb.array(kindDb.int_())),
        kindDb -> kindDb.order(kindDb.array(kindDb.int_())),
        kindDb -> kindDb.pick(kindDb.int_()),
        kindDb -> kindDb.reference(kindDb.int_()),
        kindDb -> kindDb.select(kindDb.int_()),
        kindDb -> kindDb.fold(kindDb.int_()),
        kindDb -> kindDb.array(kindDb.blob()),
        kindDb -> kindDb.array(kindDb.bool()),
        kindDb -> kindDb.array(kindDb.int_()),
        kindDb -> kindDb.array(kindDb.string()),
        kindDb -> kindDb.array(tupleType(kindDb)),
        kindDb -> kindDb.array(lambdaType(kindDb)),
        kindDb -> kindDb.array(kindDb.array(kindDb.blob())),
        kindDb -> kindDb.array(kindDb.array(kindDb.bool())),
        kindDb -> kindDb.array(kindDb.array(kindDb.int_())),
        kindDb -> kindDb.array(kindDb.array(kindDb.string())),
        kindDb -> kindDb.array(kindDb.array(tupleType(kindDb))),
        kindDb -> kindDb.array(kindDb.array(lambdaType(kindDb))));
  }

  private static BChoiceType choiceType(BKindDb kindDb) throws BytecodeException {
    return kindDb.choice(kindDb.blob(), kindDb.int_());
  }

  private static BTupleType tupleType(BKindDb kindDb) throws BytecodeException {
    return kindDb.tuple(kindDb.string(), kindDb.string());
  }

  private static BLambdaType lambdaType(BKindDb kindDb) throws BytecodeException {
    return kindDb.lambda(list(kindDb.bool(), kindDb.blob()), kindDb.string());
  }
}
