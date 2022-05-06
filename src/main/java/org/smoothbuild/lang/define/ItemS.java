package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * Item is a func param or a struct field.
 *
 * This class is immutable.
 */
public class ItemS extends EvalS {
  private final Optional<ExprS> defaultVal;
  private final ItemSigS sig;

  public ItemS(TypeS type, ModPath modPath, String name, Optional<ExprS> defaultVal, Loc loc) {
    super(type, modPath, name, loc);
    this.defaultVal = defaultVal;
    this.sig = new ItemSigS(type(), name(), defaultVal.map(ExprS::type));
  }

  public ItemSigS sig() {
    return sig;
  }

  public Optional<ExprS> defaultVal() {
    return defaultVal;
  }

  private String defaultValToString() {
    return defaultVal.map(v -> " = " + v).orElse("");
  }

  public static ImmutableList<TypeS> toTypes(List<? extends ItemS> items) {
    return map(items, ItemS::type);
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
        && Objects.equals(this.modPath(), that.modPath())
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.defaultVal, that.defaultVal)
        && Objects.equals(this.loc(), that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), modPath(), name(), defaultVal, loc());
  }
}
