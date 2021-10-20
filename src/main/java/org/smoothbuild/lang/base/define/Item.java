package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * Item is a function parameter or a struct field.
 *
 * This class is immutable.
 */
public class Item extends Referencable {
  private final Optional<Expression> defaultValue;
  private final ItemSignature signature;

  public Item(Type type, ModulePath modulePath, String name, Optional<Expression> defaultValue,
      Location location) {
    super(type, modulePath, name, location);
    this.defaultValue = defaultValue;
    this.signature = new ItemSignature(type(), name(), defaultValue.map(Expression::type));
  }

  public ItemSignature signature() {
    return signature;
  }

  public Optional<Expression> defaultValue() {
    return defaultValue;
  }

  private String defaultValueToString() {
    return defaultValue.map(v -> " = " + v).orElse("");
  }

  @Override
  public String toString() {
    return "Item(`" + type().name() + " " + name() + defaultValueToString() + "`)";
  }

  public static ImmutableList<ItemSignature> toItemSignatures(List<Item> items) {
    return map(items, Item::signature);
  }

  public static ImmutableList<Type> toTypes(List<Item> items) {
    return map(items, Item::type);
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
