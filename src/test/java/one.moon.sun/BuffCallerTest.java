package one.moon.sun;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuffCallerTest {
    private static Logger log = LoggerFactory.getLogger(BuffCallerTest.class);

    @Test
    public void test() throws InterruptedException {
        BuffCaller buffCaller = new BuffCaller(
                20, 5,
                objects -> {
                    StringBuilder builder = new StringBuilder("consume args: ");
                    for(Object[] args: objects){
                        builder.append("[");
                        for(Object arg: args){

                            builder.append(arg);
                            builder.append(", ");
                        }
                        builder.delete(builder.length()-2, builder.length());
                        builder.append("]; ");
                    }
                    log.debug(builder.toString());

                });

        buffCaller.call("hello",1);
        buffCaller.call("hello",2);
        buffCaller.call("hello",3,4);
        buffCaller.call("a");
        buffCaller.call("b");
        buffCaller.call("c");
        buffCaller.call("d");
        buffCaller.call("e");

        Thread.sleep(3000);

        buffCaller.call("hello",5,6);
        buffCaller.call("hello",7,8);
        buffCaller.call("hello",9);
        buffCaller.call("f");
        buffCaller.call("g");
        buffCaller.call("h");
        buffCaller.call("i");
        buffCaller.call("j");
        buffCaller.call("k");
        Thread.sleep(3000);
        buffCaller.call("hello",10);

        Thread.sleep(3000);
    }
}
