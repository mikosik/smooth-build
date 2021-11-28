package org.smoothbuild.lang.base.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

@Singleton
public class TypeFactoryS implements TypeFactory<TypeS> {
  private static final AnyTypeS ANY = new AnyTypeS();
  private static final BlobTypeS BLOB = new BlobTypeS();
  private static final BoolTypeS BOOL = new BoolTypeS();
  private static final IntTypeS INT = new IntTypeS();
  private static final NothingTypeS NOTHING = new NothingTypeS();
  private static final StringTypeS STRING = new StringTypeS();

  private final Sides<TypeS> sides;

  @Inject
  public TypeFactoryS() {
    this.sides = new Sides<>(any(), nothing());
  }

  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  public ImmutableList<BaseTypeS> inferableBaseTypes() {
    return ImmutableList.<BaseTypeS>builder()
        .addAll(baseTypes())
        .add(any())
        .build();
  }

  /**
   * Base types that are legal in smooth language.
   */
  public ImmutableList<BaseTypeS> baseTypes() {
    return ImmutableList.of(
        blob(),
        bool(),
        int_(),
        nothing(),
        string()
    );
  }

  @Override
  public Bounds<TypeS> unbounded() {
    return new Bounds<>(nothing(), any());
  }

  @Override
  public Bounds<TypeS> oneSideBound(Side<TypeS> side, TypeS type) {
    return switch (side) {
      case Sides.Lower l -> new Bounds<>(type, any());
      case Sides.Upper u -> new Bounds<>(nothing(), type);
    };
  }

  @Override
  public Side<TypeS> upper() {
    return sides.upper();
  }

  @Override
  public Side<TypeS> lower() {
    return sides.lower();
  }

  public AnyTypeS any() {
    return ANY;
  }

  @Override
  public ArrayTypeS array(TypeS elemType) {
    return new ArrayTypeS(elemType);
  }

  public BlobTypeS blob() {
    return BLOB;
  }

  public BoolTypeS bool() {
    return BOOL;
  }

  @Override
  public FunctionTypeS function(TypeS result, ImmutableList<TypeS> params) {
    return new FunctionTypeS(result, ImmutableList.copyOf(params));
  }

  public IntTypeS int_() {
    return INT;
  }

  public NothingTypeS nothing() {
    return NOTHING;
  }

  public StringTypeS string() {
    return STRING;
  }

  public VariableS variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'.", name);
    return new VariableS(name);
  }

  public StructTypeS struct(String name, NList<ItemSignature> fields) {
    return new StructTypeS(name, fields);
  }
}
