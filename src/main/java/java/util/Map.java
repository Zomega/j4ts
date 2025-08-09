/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package java.util;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Abstract interface for maps.
 *
 * @param <K> key type.
 * @param <V> value type.
 */
public interface Map<K, V> {

  /** Represents an individual map entry. */
  public interface Entry<K, V> {
    @Override
    boolean equals(Object o);

    K getKey();

    V getValue();

    @Override
    int hashCode();

    V setValue(V value);

    /**
     * Returns a comparator that compares {@link Map.Entry} by value using the given {@link
     * Comparator}.
     *
     * <p>The returned comparator is serializable if the specified comparator is also serializable.
     *
     * @param <K> the type of the map keys
     * @param <V> the type of the map values
     * @param cmp the value {@link Comparator}
     * @return a comparator that compares {@link Map.Entry} by the value.
     * @since 1.8
     */
    public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
      Objects.requireNonNull(cmp);
      return (Comparator<Map.Entry<K, V>>) (c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
    }
  }

  void clear();

  boolean containsKey(Object key);

  boolean containsValue(Object value);

  Set<Entry<K, V>> entrySet();

  @Override
  boolean equals(Object o);

  V get(Object key);

  @Override
  int hashCode();

  boolean isEmpty();

  Set<K> keySet();

  V put(K key, V value);

  void putAll(Map<? extends K, ? extends V> t);

  V remove(Object key);

  int size();

  Collection<V> values();

  default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> map) {
    V old = get(key);
    V next = (old == null) ? value : map.apply(old, value);
    if (next == null) {
      remove(key);
    } else {
      put(key, next);
    }
    return next;
  }

  default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    V result;
    if ((result = get(key)) == null) {
      result = mappingFunction.apply(key);
      if (result != null) put(key, result);
    }
    return result;
  }

  default V getOrDefault(Object key, V defaultValue) {
    V v;
    return (((v = get(key)) != null) || containsKey(key)) ? v : defaultValue;
  }

  default V putIfAbsent(K key, V value) {
    V v = get(key);
    if (v == null) {
      v = put(key, value);
    }

    return v;
  }

  default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    Objects.requireNonNull(function);
    for (Map.Entry<K, V> entry : entrySet()) {
      K k;
      V v;
      try {
        k = entry.getKey();
        v = entry.getValue();
      } catch (IllegalStateException ise) {
        // this usually means the entry is no longer in the map.
        throw new RuntimeException(ise);
      }

      // ise thrown from function is not a cme.
      v = function.apply(k, v);

      try {
        entry.setValue(v);
      } catch (IllegalStateException ise) {
        // this usually means the entry is no longer in the map.
        throw new RuntimeException(ise);
      }
    }
  }
}
