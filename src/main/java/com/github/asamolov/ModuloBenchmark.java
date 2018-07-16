package com.github.asamolov;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;


/**
 * Benchmark to compare different hashing functions:
 * * classic modulo hash
 * * java Hash-Map (modulo by powers of two)
 * * fibonacci hashing (see https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/)
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class ModuloBenchmark {
    @Param({"2", "3", "4", "5", "6", "7", "8", "9", "10"})
    public int bits;

    public static final int HASHED_NUMBERS = 1_000_000;

    private static int moduloHash(int x, int size, int bitsRequired) {
        return x % size;
    }
    private static int javaHash(int x, int size, int bitsRequired) {
        return (x ^ (x >>> 16)) & (size - 1);
    }

    private static int fibHash(int x, int size, int bitsRequired) {
        // using 2^30 / golden ratio  (1.61803398875)
        // 2^30 - since this is max hashmap capacity
        return (x * 663608942) >>> (32 - bitsRequired);
    }

    @Benchmark
    @OperationsPerInvocation(HASHED_NUMBERS)
    public void javaHashBench(Blackhole blackhole) {
        int size = 1 << bits;
        for (int i = 0; i < HASHED_NUMBERS; i++) {
            blackhole.consume(javaHash(i, size, bits));
        }
    }
    @Benchmark
    @OperationsPerInvocation(HASHED_NUMBERS)
    public void moduloHashBench(Blackhole blackhole) {
        int size = 1 << bits;
        for (int i = 0; i < HASHED_NUMBERS; i++) {
            blackhole.consume(moduloHash(i, size, bits));
        }
    }
    @Benchmark
    @OperationsPerInvocation(HASHED_NUMBERS)
    public void fibHashBench(Blackhole blackhole) {
        int size = 1 << bits;
        for (int i = 0; i < HASHED_NUMBERS; i++) {
            blackhole.consume(fibHash(i, size, bits));
        }
    }

}
