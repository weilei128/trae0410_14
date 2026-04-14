package com.library.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    
    private static final AtomicLong bookIdCounter = new AtomicLong(0);
    private static final AtomicLong borrowIdCounter = new AtomicLong(0);
    
    public static void initBookId(Long maxId) {
        if (maxId != null && maxId > bookIdCounter.get()) {
            bookIdCounter.set(maxId);
        }
    }
    
    public static void initBorrowId(Long maxId) {
        if (maxId != null && maxId > borrowIdCounter.get()) {
            borrowIdCounter.set(maxId);
        }
    }
    
    public static Long nextBookId() {
        return bookIdCounter.incrementAndGet();
    }
    
    public static Long nextBorrowId() {
        return borrowIdCounter.incrementAndGet();
    }
}
