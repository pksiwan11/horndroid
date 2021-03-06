/*
 * MIT License
 *
 * Copyright (c) 2017 TU Wien
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.horndroid.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.horndroid.Dalvik.DalvikClass;
import com.horndroid.Dalvik.GeneralClass;

/*
 * A lazy union of two Map.
 * Very few methods are implemented so be careful.
 */
public class LazyUnion implements Map<Integer,GeneralClass> {
    private Map<Integer,GeneralClass> map1;
    private Map<Integer,GeneralClass> map2;

    public LazyUnion(Map<Integer,GeneralClass> map1, Map<Integer,GeneralClass> map2){
        this.map1 = map1;
        this.map2 = map2;
    }
    @Override
    public int size() {
        return map1.size() + map2.size();
    }

    @Override
    public boolean isEmpty() {
        return map1.isEmpty() && map2.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (map1.containsKey(key)){
            return ((map1.get(key) instanceof DalvikClass)||(map2.containsKey(key)));
        }
        return ((map1.containsKey(key))||(map2.containsKey(key)));
    }

    @Override
    public boolean containsValue(Object value) {
        return ((map1.containsValue(value))||(map2.containsValue(value)));

    }
    @Override
    public GeneralClass get(Object key) {
        if (map1.containsKey(key)){
            GeneralClass ret = map1.get(key);
            if (ret instanceof DalvikClass){
                return ret;
            }
        }
        return map2.get(key);
    }
    
    @Override
    public GeneralClass put(Integer key, GeneralClass value) {
        throw new RuntimeException("LazyUnion");
    }
    @Override
    public GeneralClass remove(Object key) {
        throw new RuntimeException("LazyUnion");
    }
    @Override
    public void putAll(Map<? extends Integer, ? extends GeneralClass> m) {
        throw new RuntimeException("LazyUnion");       
    }
    @Override
    public void clear() {
        throw new RuntimeException("LazyUnion");   
    }
    @Override
    public Set<Integer> keySet() {
        throw new RuntimeException("LazyUnion");
    }
    @Override
    public Collection<GeneralClass> values() {
        throw new RuntimeException("LazyUnion");
    }
    @Override
    public Set<java.util.Map.Entry<Integer, GeneralClass>> entrySet() {
        throw new RuntimeException("LazyUnion");
    }
    public Collection<GeneralClass> values1() {
        return map1.values();
    }
    public Collection<GeneralClass> values2() {
        return map2.values();
    }

}
