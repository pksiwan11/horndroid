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

package com.horndroid.analysis;

import com.google.common.collect.Sets;



import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.horndroid.Dalvik.GeneralClass;
import com.horndroid.Dalvik.Instances;
import com.horndroid.Dalvik.Interfaces;
import com.horndroid.Options;
import com.horndroid.payload.ArrayData;
import com.horndroid.payload.PackedSwitch;
import com.horndroid.payload.SparseSwitch;
import com.horndroid.strings.ConstString;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;



import com.google.common.collect.Ordering;


public class Stubs {
    final private Map<Integer,GeneralClass> classes;
    final private Instances instances;
    final private Set<ArrayData> arrayDataPayload;
    final private Set<PackedSwitch> packedSwitchPayload;
    final private Set<SparseSwitch> sparseSwitchPayload;
    final private Set<Integer> staticConstructor;
    final private Set<ConstString> constStrings;
    final private Options options;
    final private Interfaces interfaces;

    public Stubs(Options options){
        //TODO: beautiful definitions like the one for classes
        this.classes = new ConcurrentHashMap<Integer, GeneralClass>();
        this.instances = new Instances();
        this.constStrings = Collections.synchronizedSet(new HashSet <ConstString>());
        this.arrayDataPayload = Collections.synchronizedSet(new HashSet <ArrayData>());
        this.packedSwitchPayload = Collections.synchronizedSet(new HashSet <PackedSwitch>());
        this.sparseSwitchPayload = Collections.synchronizedSet(new HashSet <SparseSwitch>());
        this.options = options;
        this.staticConstructor = Collections.synchronizedSet(new HashSet<Integer>());
        this.interfaces = new Interfaces();
    }
    public void process(final Set<Integer> allowed, int filterClasses, boolean filterSound){
        long startTime, endTime;

        File andFile = new File("classes.dex");
        File andFile2 = new File("classes2.dex");

        if (!andFile.exists() || !andFile2.exists()) {
            System.err.println("Can't find the file android.dex");
            System.exit(1);
        }
        DexBackedDexFile dexFile = null;
        DexBackedDexFile dexFile2 = null;
        try {
            System.out.println("Loading dex files....");
            startTime = System.nanoTime();
            dexFile = DexFileFactory.loadDexFile(andFile, options.apiLevel, false);
            dexFile2 = DexFileFactory.loadDexFile(andFile2, options.apiLevel, false);
            if (dexFile.isOdexFile() || dexFile2.isOdexFile()) {
                System.err.println("Error: Odex files are not supported");
            }
            endTime = System.nanoTime();
            System.out.println("done in " + Long.toString((endTime - startTime) / 1000000) + " milliseconds");
        } catch (IOException e) {
            System.err.println("Error: Loading dex file failed!");
            System.exit(1);
        }
//        List<? extends ClassDef> classDefs = Ordering.natural().sortedCopy(dexFile.getClasses(), dexFile2.getClasses());
        System.out.println("union-ing and sorting dex classes...");
        startTime = System.nanoTime();
        List<? extends ClassDef> classDefs = Ordering.natural().sortedCopy(Sets.union(dexFile.getClasses(), dexFile2.getClasses()));
        endTime = System.nanoTime();
        System.out.println("done in " + Long.toString((endTime - startTime) / 1000000) + " milliseconds");

        System.out.println("data extracting...");
        startTime = System.nanoTime();
        DataExtraction de = new DataExtraction(classes, instances, arrayDataPayload, packedSwitchPayload, sparseSwitchPayload, staticConstructor, constStrings, new HashSet<Integer>(),
                false, null, null, null, null, interfaces,
                allowed, filterClasses, filterSound);
        de.collectData(classDefs);
        endTime = System.nanoTime();
        System.out.println("done in " + Long.toString((endTime - startTime) / 1000000) + " milliseconds");

    }
    public Map<Integer,GeneralClass> getClasses() {
        return classes;
    }
    
    
    public Instances getInstances(){
        return instances;
    }
    
    public Set<PackedSwitch> getPackedSwitchPayload(){
        return packedSwitchPayload;
    }
    
    public Set<SparseSwitch> getSparseSwitchPayload(){
        return sparseSwitchPayload;
    }
    
    public Set<Integer> getStaticConstructor(){
        return staticConstructor;
    }
    
    public Set<ConstString> getConstStrings(){
        return constStrings;
    }
    
    public Set<ArrayData> getArrayDataPayload(){
        return arrayDataPayload;
    }

}
