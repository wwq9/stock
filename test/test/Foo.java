package test;

import org.apache.commons.lang.math.RandomUtils;

public class Foo {
	public static void foo(){
		try {
			while(true){
				Thread.sleep(RandomUtils.nextInt(1000));
				System.out.println("foo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void foo1(){
		try {
			synchronized ("") {
				while(true){
				Thread.sleep(RandomUtils.nextInt(1000));
				System.out.println("foo1");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static synchronized void foo2(){
		try {
			while(true){
			Thread.sleep(RandomUtils.nextInt(1000));
			System.out.println("foo2");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
