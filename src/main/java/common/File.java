package common;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class File {
    private byte[] data;
    private String name;

    public File(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public File(String path) {
        path = path.replaceAll(Pattern.quote("\\"), "/");
        name = getFilenameWithExtension(path);

        System.out.println(path);
        java.io.File nativeFile = new java.io.File(path);
        try {
            data = new byte[(int)nativeFile.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(nativeFile));
            dis.readFully(data);
            dis.close();
        }
        catch (Exception exception) {
            System.err.println(exception.toString());
        }
    }

    public boolean isLoaded() {
        return data != null;
    }

    public byte[] getBinaryData() {
        return data;
    }

    public static String getFilenameWithExtension(String path) {
        String[] arr = path.split(Pattern.quote("/"));
        return arr[arr.length - 1];
    }

    public String getFilenameWithExtension() {
        return name;
    }

    public String getFilenameWithoutExtension() {
        return getFilenameWithExtension().split(Pattern.quote("."))[0];
    }

    public String getExtension() {
        String[] arr = getFilenameWithExtension().split(Pattern.quote("."));
        return arr[arr.length - 1];
    }

    public void save(String path) {
        try {
            Path p = Paths.get(path);
            Files.createDirectories(p.getParent());
            Files.write(p, data);
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public String toString() {
        return new String(data);
    }
}