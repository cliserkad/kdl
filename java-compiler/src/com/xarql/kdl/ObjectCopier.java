package com.xarql.kdl;

import java.io.*;

public class ObjectCopier {

    public static void main(String[] args) throws Exception {
        Path p1 = new Path("dev/jokes/funny");
        Path p2 = copyAndCheck(p1);
        System.out.println(p1);
        System.out.println(p2);
    }

    public static <T extends Serializable> T copy(T input) throws IOException, ClassNotFoundException {
        // Create byte buffer
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // write input to byte buffer
        new ObjectOutputStream(bos).writeObject(input);
        // read byte buffer to create the copy
        return (T) new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())).readObject();
    }

    public static <T extends Serializable> T copyAndCheck(T input) throws IOException, ClassNotFoundException {
        final T copy = copy(input);
        if(copy == input)
            throw new IllegalStateException("The generated copy shares the memory address of the input");
        if(!copy.equals(input))
            throw new IllegalStateException("The generated copy reported that it isn't equal to the input");
        return copy;
    }

}
