package com.davenonymous.libnonymous.utils;

public interface IThreeToOneSupplier<inA, inB, inC, outD> {
    public outD apply(inA a, inB b, inC c);
}