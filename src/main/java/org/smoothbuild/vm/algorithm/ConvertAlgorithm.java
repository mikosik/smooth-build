package org.smoothbuild.vm.algorithm;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.IsAssignable.isAssignable;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.allMatch;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.FuncB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.IsAssignable;
import org.smoothbuild.bytecode.type.TypingB;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class ConvertAlgorithm extends Algorithm {

  public ConvertAlgorithm(TypeB outputT, TypingB typing) {
    super(outputT);
  }

  @Override
  public Hash hash() {
    return AlgorithmHashes.convertAlgorithmHash(outputT());
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    checkArgument(input.items().size() == 1);
    var targetT = outputT();
    var val = input.items().get(0);
    checkArgument(!targetT.equals(val.type()));
    var converted = convert(targetT, val, nativeApi.factory());
    return new Output(converted, nativeApi.messages());
  }

  private CnstB convert(TypeB targetT, CnstB cnst, BytecodeF factory) {
    if (targetT.equals(cnst.type())) {
      return cnst;
    } else {
      return switch (targetT) {
        case ArrayTB a -> convertArray(a, (ArrayB) cnst, factory);
        case FuncTB f -> convertFunc(f, (FuncB) cnst, factory);
        case TupleTB t -> convertTuple(t, (TupleB) cnst, factory);
        default -> throw unexpectedCaseExc(targetT);
      };
    }
  }

  private ArrayB convertArray(ArrayTB targetT, ArrayB array, BytecodeF factory) {
    var builder = factory.arrayBuilder(targetT);
    TypeB elemTargetT = targetT.elem();
    array.elems(CnstB.class)
        .forEach(e -> builder.add(convert(elemTargetT, e, factory)));
    return builder.build();
  }

  private FuncB convertFunc(FuncTB targetT, FuncB func, BytecodeF factory) {
    var sourceT = func.type();
    checkArgument(isAssignable(targetT.res(), sourceT.res()));
    checkArgument(allMatch(sourceT.params(), targetT.params(), IsAssignable::isAssignable));
    return factory.func(targetT, convertBodyIfNeeded(targetT, func, factory));
  }

  private ObjB convertBodyIfNeeded(FuncTB targetT, FuncB func, BytecodeF factory) {
    var body = func.body();
    if (body instanceof CnstB cnstB && !targetT.res().equals(body.type())) {
      return convert(targetT.res(), cnstB, factory);
    }
    return body;
  }

  private TupleB convertTuple(TupleTB targetT, TupleB tuple, BytecodeF factory) {
    var targetTs = targetT.items();
    var items = tuple.items();
    checkArgument(targetTs.size() == items.size());
    var builder = ImmutableList.<CnstB>builder();
    for (int i = 0; i < targetTs.size(); i++) {
      builder.add(convert(targetTs.get(i), items.get(i), factory));
    }
    return factory.tuple(targetT, builder.build());
  }
}
