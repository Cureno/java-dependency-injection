package com.blexven.dependency_injection;

public class IndirectHello {

    public static void main(String[] args) {

        Greeting greeting = new Hello();

        greeting.show();

    }
}
