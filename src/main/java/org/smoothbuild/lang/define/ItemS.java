package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tanal;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.TypelikeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

/**
 * Item is a func param or a struct field.
 * This class is immutable.
 */
public final class ItemS extends Tanal implements RefableS {
  private final Optional<ExprS> body;
  private final ItemSigS sig;

  public ItemS(TypeS type, String name, Optional<ExprS> body, Loc loc) {
    super(type, name, loc);
    this.body = body;
    this.sig = new ItemSigS(type(), name());
  }

  public ItemS(ItemSigS sig, Optional<ExprS> body, Loc loc) {
    super(sig.type(), sig.nameO().get(), loc);
    this.body = body;
    this.sig = sig;
  }

  public static NList<ItemS> mapParams(NList<ItemS> params, Function<VarS, TypeS> mapper) {
    return params.map(i -> i.mapVars(mapper));
  }

  @Override
  public ModPath modPath() {
    throw new UnsupportedOperationException();
  }

  public ItemSigS sig() {
    return sig;
  }

  public Optional<ExprS> body() {
    return body;
  }

  private String defaultValToString() {
    return body.map(v -> " = " + v).orElse("");
  }

  public static ImmutableList<TypeS> toTypes(List<? extends ItemS> items) {
    return map(items, ItemS::type);
  }

  @Override
  public TypelikeS typelike() {
    return type();
  }

  public ItemS mapVars(Function<VarS, TypeS> mapper) {
    return new ItemS(type().mapVars(mapper), name(), body.map(b -> b.mapVars(mapper)), loc());
  }

  @Override
  public String toString() {
    return "Item(`" + type().name() + " " + name() + defaultValToString() + "`)";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return (o instanceof ItemS that)
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.body, that.body)
        && Objects.equals(this.loc(), that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), body, loc());
  }
}
