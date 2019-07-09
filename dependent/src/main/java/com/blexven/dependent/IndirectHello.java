package com.blexven.dependent;

import com.blexven.injector.PleaseWork;

public class IndirectHello {

    @PleaseWork // this is being picked up by "mvn clean package"
    private Greeting greeting;

    public static void main(String[] args) {

        IndirectHello indirectHello = new IndirectHello();
        indirectHello.greeting.show();

    }
}
