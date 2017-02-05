package strickli.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Hello world!
 *
 */
public class Main {
	private static final ThreadLocal<Kryo> threadKryo =
	         new ThreadLocal<Kryo>() {
	             @Override protected Kryo initialValue() {
	                 return new Kryo();
	         }
	     };

    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );
        Foo f = new Foo("aaaa", "bbbb", Integer.valueOf(17), Long.valueOf(23));
        System.out.println("Foo: " + f.toString());
        String s0 = serializedString(f);
        System.out.println("Foo serialized string: " + s0.length() + " " + s0);
        
        System.out.println("Next registration id: "+ threadKryo.get().getNextRegistrationId());
        int nxt = 100 + threadKryo.get().getNextRegistrationId();
        System.out.println("Foo class name " + Foo.class.getName());
        threadKryo.get().register(Foo.class, nxt);
        String s1 = kryoString(f);
        System.out.println("Foo kryo-ized  string: " + s1.length() + " " + s1);
        
        
        byte [] data = Base64.getDecoder().decode( s1 );
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        Input input = new Input(bais);
        //Foo foo0 = kryo.readObject(input, Foo.class);
        Foo foo1 = get(input);
        input.close();
        //System.out.println("newFoo: " + foo0.toString());
        System.out.println("newFoo: " + foo1.toString());
    }
    @SuppressWarnings("unchecked")
	static <T> T get(Input i) {
    	return (T)threadKryo.get().readClassAndObject(i);
    }
    // =================================
    private static String kryoString(Foo f) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream( );
        Output output = new Output( baos );
        threadKryo.get().writeClassAndObject(output, f);
        //kryo.writeObject(output, f);
        output.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
    // =================================
    private static String serializedString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
    // =================================

    // 24 bytes w/ name Foo
 // 24 bytes w/ name Foo01234567890123456789
    // not registered w/ name Foo01234567890123456789 84 bytes
 // not registered w/ name Foo 56 bytes
    @NoArgsConstructor @AllArgsConstructor @ToString
    private static class Foo implements Serializable {
		private static final long serialVersionUID = 1L;
	    String f0;
    	String f1;
    	Integer f2;
    	Long f3;
    }
}
