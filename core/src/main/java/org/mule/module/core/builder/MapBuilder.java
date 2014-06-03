package org.mule.module.core.builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by machaval on 6/2/14.
 */
public class MapBuilder<K, V>
{

    private Map<K, V> result = new HashMap<K, V>();
    private K key;

    public MapBuilder(K key)
    {
        this.key = key;
    }


    public MapBuilder<K, V> map(K key)
    {
        this.key = key;
        return this;
    }

    public MapBuilder<K, V> to(V value)
    {
        if (key == null)
        {
            throw new IllegalStateException("Key is null. Invoke map 'method' before 'to'.");
        }
        result.put(key, value);
        key = null;
        return this;
    }


    public Map<K, V> build()
    {
        return result;
    }
}
