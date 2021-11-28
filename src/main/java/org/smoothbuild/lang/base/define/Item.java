package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.expr.ExprS;

import com.google.common.collect.ImmutableList;

/**
 * Item is a function param or a struct field.
 *
 * This class is immutable.
 */
public class Item extends EvaluableImplS {
  private final Optional<ExprS> defaultValue;
  private final ItemSignature signature;

  public Item(TypeS type, ModulePath modulePath, String name, Optional<ExprS> defaultValue,
      Location location) {
    super(type, modulePath, name, location);
    this.defaultValue = defaultValue;
    this.signature = new ItemSignature(type(), name(), defaultValue.map(ExprS::type));
  }

  public ItemSignature signature() {
    return signature;
  }

  public Optional<ExprS> defaultValue() {
    return defaultValue;
  }

  private String defaultValueToString() {
    return defaultValue.map(v -> " = " + v).orElse("");
  }

  public static ImmutableList<TypeS> toTypes(List<Item> items) {
    return map(items, Item::type);
  }

  @Override
  public String toString() {
    return "Item(`" + type().name() + " " + name() + defaultValueToString() + "`)";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return (o instanceof Item that)
        && Objects.equals(this.type(), that.type())
        && Objects.equals(this.modulePath(), that.modulePath())
        && Objects.equals(this.name(), that.name())
        && Objects.equals(this.defaultValue, that.defaultValue)
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), modulePath(), name(), defaultValue, location());
  }
}
