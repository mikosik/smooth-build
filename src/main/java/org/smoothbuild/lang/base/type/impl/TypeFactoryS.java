package org.smoothbuild.lang.base.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.base.type.api.AbstractTypeFactory;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@Singleton
public class TypeFactoryS extends AbstractTypeFactory<TypeS> {
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
  public ImmutableSet<BaseTypeS> inferableBaseTypes() {
    return ImmutableSet.<BaseTypeS>builder()
        .addAll(baseTypes())
        .add(any())
        .build();
  }


  /**
   * Base types that are legal in smooth language.
   */
  public ImmutableSet<BaseTypeS> baseTypes() {
    return ImmutableSet.of(
        blob(),
        bool(),
        int_(),
        nothing(),
        string()
    );
  }

  @Override
  public Side<TypeS> upper() {
    return sides.upper();
  }

  @Override
  public Side<TypeS> lower() {
    return sides.lower();
  }

  @Override
  public AnyTypeS any() {
    return ANY;
  }

  @Override
  public ArrayTypeS array(TypeS elemType) {
    return new ArrayTypeS(elemType);
  }

  @Override
  public BlobTypeS blob() {
    return BLOB;
  }

  @Override
  public BoolTypeS bool() {
    return BOOL;
  }

  @Override
  public FunctionTypeS function(TypeS result, ImmutableList<? extends TypeS> parameters) {
    return new FunctionTypeS(result, ImmutableList.copyOf(parameters));
  }

  @Override
  public IntTypeS int_() {
    return INT;
  }

  @Override
  public NothingTypeS nothing() {
    return NOTHING;
  }

  @Override
  public StringTypeS string() {
    return STRING;
  }

  @Override
  public StructTypeS struct(String name, NamedList<? extends TypeS> fields) {
    return new StructTypeS(name, (NamedList<TypeS>) fields);
  }

  @Override
  public VariableS variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'.", name);
    return new VariableS(name);
  }
}
