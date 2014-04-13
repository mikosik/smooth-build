package org.smoothbuild.db.hashed;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class EnumValues<T> {
  private final ImmutableList<T> values;
  private final ImmutableMap<T, Byte> map;

  @SuppressWarnings("unchecked")
  public EnumValues(T... values) {
    this.values = ImmutableList.copyOf(values);
    this.map = createMap(values);
  }

  private static <T> ImmutableMap<T, Byte> createMap(T[] values) {
    Builder<T, Byte> builder = ImmutableMap.builder();
    for (int i = 0; i < values.length; i++) {
      builder.put(values[i], Byte.valueOf((byte) i));
    }
    return builder.build();
  }

  public boolean isValidByte(byte byteValue) {
    return 0 <= byteValue && byteValue < values.size();
  }

  public T byteToValue(byte byteValue) {
    checkArgument(isValidByte(byteValue));
    return values.get(byteValue);
  }

  public byte valueToByte(T value) {
    Byte byteValue = map.get(value);
    if (byteValue == null) {
      throw new IllegalArgumentException("Value " + value + " does not belong to that EnumValues.");
    } else {
      return byteValue.byteValue();
    }
  }
}
