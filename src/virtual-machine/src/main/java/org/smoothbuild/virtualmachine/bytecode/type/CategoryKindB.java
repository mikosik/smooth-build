package org.smoothbuild.virtualmachine.bytecode.type;

import java.util.function.BiFunction;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.AbstFuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.ArrayKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.BaseKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.FuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.OperKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.TupleKindB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CallCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CombineCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OperCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OrderCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.PickCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.ReferenceCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.SelectCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IfFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.LambdaCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.MapFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public abstract sealed class CategoryKindB
    permits AbstFuncKindB, ArrayKindB, BaseKindB, FuncKindB, OperKindB, TupleKindB {
  public abstract static sealed class BaseKindB extends CategoryKindB
      permits BlobKindB, BoolKindB, IntKindB, StringKindB {
    private BaseKindB(byte marker) {
      super(marker);
    }
  }

  public static sealed class OperKindB<T extends OperCB> extends CategoryKindB
      permits CallKindB, CombineKindB, OrderKindB, PickKindB, SelectKindB, ReferenceKindB {
    private final BiFunction<Hash, TypeB, T> constructor;
    private final Class<? extends TypeB> dataClass;

    private OperKindB(
        byte marker, BiFunction<Hash, TypeB, T> constructor, Class<? extends TypeB> dataClass) {
      super(marker);
      this.constructor = constructor;
      this.dataClass = dataClass;
    }

    public BiFunction<Hash, TypeB, T> constructor() {
      return constructor;
    }

    public Class<? extends TypeB> dataClass() {
      return dataClass;
    }
  }

  public static final class BlobKindB extends BaseKindB {
    BlobKindB() {
      super((byte) 0);
    }
  }

  public static final class BoolKindB extends BaseKindB {
    BoolKindB() {
      super((byte) 1);
    }
  }

  public static final class IntKindB extends BaseKindB {
    IntKindB() {
      super((byte) 2);
    }
  }

  public static final class StringKindB extends BaseKindB {
    StringKindB() {
      super((byte) 3);
    }
  }

  public static final class ArrayKindB extends CategoryKindB {
    ArrayKindB() {
      super((byte) 4);
    }
  }

  public static final class TupleKindB extends CategoryKindB {
    TupleKindB() {
      super((byte) 5);
    }
  }

  public static final class FuncKindB extends CategoryKindB {
    FuncKindB() {
      super((byte) 16);
    }
  }

  public abstract static sealed class AbstFuncKindB<T extends FuncCB> extends CategoryKindB {
    private final BiFunction<Hash, FuncTB, T> instantiator;

    private AbstFuncKindB(byte marker, BiFunction<Hash, FuncTB, T> instantiator) {
      super(marker);
      this.instantiator = instantiator;
    }

    public BiFunction<Hash, FuncTB, T> instantiator() {
      return instantiator;
    }
  }

  public static final class LambdaKindB extends AbstFuncKindB<LambdaCB> {
    LambdaKindB() {
      super((byte) 6, LambdaCB::new);
    }
  }

  public static final class NativeFuncKindB extends AbstFuncKindB<NativeFuncCB> {
    NativeFuncKindB() {
      super((byte) 7, NativeFuncCB::new);
    }
  }

  public static final class OrderKindB extends OperKindB<OrderCB> {
    OrderKindB() {
      super((byte) 8, OrderCB::new, ArrayTB.class);
    }
  }

  public static final class CombineKindB extends OperKindB<CombineCB> {
    CombineKindB() {
      super((byte) 9, CombineCB::new, TupleTB.class);
    }
  }

  public static final class SelectKindB extends OperKindB<SelectCB> {
    SelectKindB() {
      super((byte) 10, SelectCB::new, TypeB.class);
    }
  }

  public static final class CallKindB extends OperKindB<CallCB> {
    CallKindB() {
      super((byte) 11, CallCB::new, TypeB.class);
    }
  }

  public static final class PickKindB extends OperKindB<PickCB> {
    PickKindB() {
      super((byte) 12, PickCB::new, TypeB.class);
    }
  }

  public static final class IfFuncKindB extends AbstFuncKindB<IfFuncCB> {
    IfFuncKindB() {
      super((byte) 13, IfFuncCB::new);
    }
  }

  public static final class ReferenceKindB extends OperKindB<ReferenceCB> {
    ReferenceKindB() {
      super((byte) 14, ReferenceCB::new, TypeB.class);
    }
  }

  public static final class MapFuncKindB extends AbstFuncKindB<MapFuncCB> {
    MapFuncKindB() {
      super((byte) 15, MapFuncCB::new);
    }
  }

  private final byte marker;

  private CategoryKindB(byte marker) {
    this.marker = marker;
  }

  public byte marker() {
    return marker;
  }

  public static CategoryKindB fromMarker(byte marker) {
    return switch (marker) {
      case 0 -> CategoryKinds.BLOB;
      case 1 -> CategoryKinds.BOOL;
      case 2 -> CategoryKinds.INT;
      case 3 -> CategoryKinds.STRING;
      case 4 -> CategoryKinds.ARRAY;
      case 5 -> CategoryKinds.TUPLE;
      case 6 -> CategoryKinds.LAMBDA;
      case 7 -> CategoryKinds.NATIVE_FUNC;
      case 8 -> CategoryKinds.ORDER;
      case 9 -> CategoryKinds.COMBINE;
      case 10 -> CategoryKinds.SELECT;
      case 11 -> CategoryKinds.CALL;
      case 12 -> CategoryKinds.PICK;
      case 13 -> CategoryKinds.IF_FUNC;
      case 14 -> CategoryKinds.REFERENCE;
      case 15 -> CategoryKinds.MAP_FUNC;
      case 16 -> CategoryKinds.FUNC;
      default -> null;
    };
  }

  @Override
  public String toString() {
    return "CategoryKindB{" + "name='" + getClass().getSimpleName() + '\'' + ", marker=" + marker
        + '}';
  }
}
