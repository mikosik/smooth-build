package org.smoothbuild.vm.job.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.slib.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.allMatch;

import org.smoothbuild.bytecode.ByteCodeFactory;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ConvertAlgorithm extends Algorithm {
  private final TypingB typing;

  public ConvertAlgorithm(TypeB outputT, TypingB typing) {
    super(outputT);
    this.typing = typing;
  }

  @Override
  public Hash hash() {
    return AlgorithmHashes.convertAlgorithmHash(outputT());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    checkArgument(input.vals().size() == 1);
    var targetT = outputT();
    var val = input.vals().get(0);
    checkArgument(!targetT.equals(val.type()));
    var converted = convert(targetT, val, nativeApi.factory());
    return new Output(converted, nativeApi.messages());
  }

  private ValB convert(TypeB targetT, ValB val, ByteCodeFactory factory) {
    if (targetT.equals(val.type())) {
      return val;
    } else {
      return switch (targetT) {
        case ArrayTB a -> convertArray(a, (ArrayB) val, factory);
        case FuncTB f -> convertFunc(f, (FuncB) val, factory);
        case TupleTB t -> convertTuple(t, (TupleB) val, factory);
        default -> throw unexpectedCaseExc(targetT);
      };
    }
  }

  private ArrayB convertArray(ArrayTB targetT, ArrayB array, ByteCodeFactory factory) {
    var builder = factory.arrayBuilder(targetT);
    TypeB elemTargetT = targetT.elem();
    array.elems(ValB.class)
        .forEach(e -> builder.add(convert(elemTargetT, e, factory)));
    return builder.build();
  }

  private FuncB convertFunc(FuncTB targetT, FuncB func, ByteCodeFactory factory) {
    var sourceT = func.type();
    checkArgument(isAssignable(targetT.res(), sourceT.res()));
    checkArgument(allMatch(sourceT.params(), targetT.params(), this::isAssignable));
    return factory.func(targetT, convertBodyIfNeeded(targetT, func, factory));
  }

  private ObjB convertBodyIfNeeded(FuncTB targetT, FuncB func, ByteCodeFactory factory) {
    var body = func.body();
    if (body instanceof ValB valB && !targetT.res().equals(body.type())) {
      return convert(targetT.res(), valB, factory);
    }
    return body;
  }

  private boolean isAssignable(TypeB targetT, TypeB sourceT) {
    return typing.isAssignable(targetT, sourceT);
  }

  private TupleB convertTuple(TupleTB targetT, TupleB tuple, ByteCodeFactory factory) {
    var targetTs = targetT.items();
    var items = tuple.items();
    checkArgument(targetTs.size() == items.size());
    var builder = ImmutableList.<ValB>builder();
    for (int i = 0; i < targetTs.size(); i++) {
      builder.add(convert(targetTs.get(i), items.get(i), factory));
    }
    return factory.tuple(targetT, builder.build());
  }
}
