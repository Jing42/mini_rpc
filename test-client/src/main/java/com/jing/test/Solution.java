package com.jing.test;

import java.util.*;


public class Solution {
    public int minKBitFlips(int[] A, int K) {
        int countZero = 0;
        int n = A.length;
        if((countZero % 2 == 1) && (K % 2 == 0)) {
            return -1;
        }
        int res = 0;
        Set<Integer> occured = new HashSet<>();
        for(int i = 0; i < A.length; i++) {
            if(A[i] == 0) countZero++;
        }
        while(countZero != 0) {
            res++;
            if(countZero >= 2 * K) {
                countZero -= K;
            }
        }
        return res;
    }
}
