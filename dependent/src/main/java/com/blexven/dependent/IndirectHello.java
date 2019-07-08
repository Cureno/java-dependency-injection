package com.blexven.dependent;

import com.blexven.injector.PleaseWork;

@PleaseWork // this is being picked up by "mvn clean package"
public class IndirectHello {

    @PleaseWork // this is being picked up by "mvn clean package"
    public static void main(String[] args) {

        @PleaseWork // THIS IS NOT BEING PICKED UP
                Greeting greeting = new Hello();

        greeting.show();

    }
}
