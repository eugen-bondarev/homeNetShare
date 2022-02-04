package connection;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Broadcast {
    private String name;
    private byte[] content;

    private static byte[] readBytes(InputStream input, int size) throws IOException {
        return input.readNBytes(size);
    }

    private static int readInt(InputStream input) throws IOException {
        byte[] bytes = readBytes(input, 4);
        return ByteBuffer.wrap(bytes).getInt();
    }

    private static String readString(InputStream input, int size) throws IOException {
        byte[] bytes = readBytes(input, size);
        return new String(bytes);
    }

    public static Broadcast readFromInputStream(InputStream input) throws IOException {
        Broadcast file = new Broadcast();
        int nameSize = readInt(input);
        file.name = readString(input, nameSize);
        int contentSize = readInt(input);
        file.content = readBytes(input, contentSize);
        return file;
    }

    private static void writeInt(OutputStream output, int value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        output.write(bytes);
    }

    private static void writeString(OutputStream output, String value) throws IOException {
        output.write(value.getBytes());
    }

    public void broadcast(OutputStream output) throws IOException {
        writeInt(output, name.length());
        writeString(output, name);
        writeInt(output ,content.length);
        output.write(content);
    }

    public void save(String path) throws IOException {
        Path p = Paths.get(path);
        Files.write(p, content);
    }

    public static Broadcast open(String path) throws IOException {
        Broadcast result = new Broadcast();
        File nativeFile = new File(path);
        result.content = new byte[(int)nativeFile.length()];
        result.name = path;
        DataInputStream dis = new DataInputStream(new FileInputStream(nativeFile));
        dis.readFully(result.content);
        dis.close();
        return result;
    }

    public String getName() {
        return name;
    }
}