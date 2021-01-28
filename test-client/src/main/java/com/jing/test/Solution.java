package com.jing.test;

import java.util.ArrayList;
import java.util.List;

class Foo {
    int a;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}

public class Solution {
    public static void main(String[] args) {
        List<Foo> fooList = new ArrayList<>();
        Foo foo = new Foo();
        for(int i = 0; i < 20; i++) {
            foo.setA(i);
            fooList.add(foo);
        }
        for (Foo foo1 : fooList) {
            System.out.println(foo1.getA());
        }
    }


}
