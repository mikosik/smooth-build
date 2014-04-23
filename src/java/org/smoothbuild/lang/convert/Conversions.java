package org.smoothbuild.lang.convert;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public class Conversions {

  /**
   * Maps smooth type to a map that contains keys being super-types of that type
   * and values being converters to each super-type.
   */
  private static final ImmutableMap<SType<?>, ImmutableMap<SType<?>, Converter<?, ?>>> map =
      createMap();

  public static ImmutableSet<SType<?>> superTypesOf(SType<?> type) {
    return map.get(type).keySet();
  }

  public static boolean canConvert(SType<?> from, SType<?> to) {
    if (from == to) {
      return true;
    }
    return map.get(from).containsKey(to);
  }

  public static <S extends SValue, T extends SValue> Converter<S, T> converter(SType<S> from,
      SType<T> to) {
    ImmutableMap<SType<?>, Converter<?, ?>> availableConverters = map.get(from);

    /*
     * This is safe as we created map in correct way and do not allow changing
     * it.
     */
    @SuppressWarnings("unchecked")
    Converter<S, T> result = (Converter<S, T>) availableConverters.get(to);
    return result;
  }

  private static ImmutableMap<SType<?>, ImmutableMap<SType<?>, Converter<?, ?>>> createMap() {
    Builder<SType<?>, ImmutableMap<SType<?>, Converter<?, ?>>> builder = ImmutableMap.builder();

    builder.put(STRING, Empty.typeConverterMap());
    builder.put(BLOB, Empty.typeConverterMap());
    builder.put(FILE, convertersMap(new FileToBlobConverter()));
    builder.put(NOTHING, Empty.typeConverterMap());

    builder.put(STRING_ARRAY, Empty.typeConverterMap());
    builder.put(BLOB_ARRAY, Empty.typeConverterMap());
    builder.put(FILE_ARRAY, convertersMap(new FileArrayToBlobArrayConverter()));

    Converter<?, ?> nilToStringArray = new NilToTypedArrayConverter<SString>(STRING_ARRAY);
    Converter<?, ?> nilToBlobArray = new NilToTypedArrayConverter<SBlob>(BLOB_ARRAY);
    Converter<?, ?> nilToFileArray = new NilToTypedArrayConverter<SFile>(FILE_ARRAY);

    builder.put(NIL, convertersMap(nilToStringArray, nilToBlobArray, nilToFileArray));

    return builder.build();
  }

  private static ImmutableMap<SType<?>, Converter<?, ?>> convertersMap(
      Converter<?, ?>... converters) {
    Builder<SType<?>, Converter<?, ?>> builder = ImmutableMap.builder();
    for (Converter<?, ?> converter : converters) {
      builder.put(converter.targetType(), converter);
    }
    return builder.build();
  }
}
