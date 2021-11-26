package org.smoothbuild.db.object.type;

import static org.smoothbuild.db.object.type.base.TypeKindH.ABSTRACT_FUNCTION;
import static org.smoothbuild.db.object.type.base.TypeKindH.DEFINED_FUNCTION;
import static org.smoothbuild.db.object.type.base.TypeKindH.IF_FUNCTION;
import static org.smoothbuild.db.object.type.base.TypeKindH.MAP_FUNCTION;
import static org.smoothbuild.db.object.type.base.TypeKindH.NATIVE_FUNCTION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.base.TypeKindH;
import org.smoothbuild.db.object.type.val.AbstractFunctionTypeH;
import org.smoothbuild.db.object.type.val.DefinedFunctionTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.IfFunctionTypeH;
import org.smoothbuild.db.object.type.val.MapFunctionTypeH;
import org.smoothbuild.db.object.type.val.NativeFunctionTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

abstract class FunctionKind<T extends FunctionTypeH> {
  public static final FunctionKind<AbstractFunctionTypeH> ABSTRACT_KIND =
      new FunctionKind<>(ABSTRACT_FUNCTION) {
        @Override
        public AbstractFunctionTypeH newInstance(Hash hash, TypeHV result, TupleTypeH params) {
          return new AbstractFunctionTypeH(hash, result, params);
        }
      };
  public static final FunctionKind<NativeFunctionTypeH> NATIVE_KIND =
      new FunctionKind<>(NATIVE_FUNCTION) {
        @Override
        public NativeFunctionTypeH newInstance(Hash hash, TypeHV result, TupleTypeH params) {
          return new NativeFunctionTypeH(hash, result, params);
        }
      };
  public static final FunctionKind<DefinedFunctionTypeH> DEFINED_KIND =
      new FunctionKind<>(DEFINED_FUNCTION) {
        @Override
        public DefinedFunctionTypeH newInstance(Hash hash, TypeHV result, TupleTypeH params) {
          return new DefinedFunctionTypeH(hash, result, params);
        }
      };
  public static final FunctionKind<IfFunctionTypeH> IF_KIND =
      new FunctionKind<>(IF_FUNCTION) {
        @Override
        public IfFunctionTypeH newInstance(Hash hash, TypeHV result, TupleTypeH params) {
          return new IfFunctionTypeH(hash, result, params);
        }
      };
  public static final FunctionKind<MapFunctionTypeH> MAP_KIND =
      new FunctionKind<>(MAP_FUNCTION) {
        @Override
        public MapFunctionTypeH newInstance(Hash hash, TypeHV result, TupleTypeH params) {
          return new MapFunctionTypeH(hash, result, params);
        }
      };

  private final TypeKindH kind;

  private FunctionKind(TypeKindH kind) {
    this.kind = kind;
  }

  public static FunctionKind<?> from(TypeKindH kind) {
    return switch (kind) {
      case ABSTRACT_FUNCTION -> ABSTRACT_KIND;
      case DEFINED_FUNCTION -> DEFINED_KIND;
      case NATIVE_FUNCTION -> NATIVE_KIND;
      case IF_FUNCTION -> IF_KIND;
      case MAP_FUNCTION -> MAP_KIND;
      default -> throw new IllegalArgumentException();
    };
  }

  public TypeKindH kind() {
    return kind;
  }

  public abstract T newInstance(Hash hash, TypeHV result, TupleTypeH params);
}
