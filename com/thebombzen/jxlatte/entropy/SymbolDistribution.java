package com.thebombzen.jxlatte.entropy;

import java.io.IOException;

import com.thebombzen.jxlatte.io.Bitreader;

public abstract class SymbolDistribution {
    protected HybridUintConfig config;
    protected int logBucketSize;
    protected int alphabetSize;
    protected int logAlphabetSize;

    public abstract int readSymbol(Bitreader reader) throws IOException;
}
