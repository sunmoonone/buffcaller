# buffcaller

buff method call arguments to a queue and then consume arguments in batch

```java

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
```


## output
```
Running one.moon.sun.BuffCallerTest
15:39:03.693 [Thread-0] DEBUG one.moon.sun.BuffCallerTest - consume args: [hello, 1]; [hello, 2]; [hello, 3, 4]; [a]; [b];
15:39:03.725 [Thread-0] DEBUG one.moon.sun.BuffCallerTest - consume args: [c]; [d]; [e];
15:39:06.693 [Thread-0] DEBUG one.moon.sun.BuffCallerTest - consume args: [hello, 5, 6]; [hello, 7, 8]; [hello, 9];
15:39:06.709 [Thread-0] DEBUG one.moon.sun.BuffCallerTest - consume args: [f]; [g]; [h]; [i]; [j];
15:39:06.756 [Thread-0] DEBUG one.moon.sun.BuffCallerTest - consume args: [k];
15:39:09.697 [Thread-0] DEBUG one.moon.sun.BuffCallerTest - consume args: [hello, 10];
```
