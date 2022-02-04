package connection.messages;

import common.File;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FileMessage extends Message {
    public FileMessage(File file) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + file.getFilenameWithExtension().length() + 4 + file.getBinaryData().length);
        int offset = 0;

        byteBuffer.put(offset, ByteBuffer.allocate(4).putInt(file.getFilenameWithExtension().length()).array());
        offset += 4;

        byteBuffer.put(offset, file.getFilenameWithExtension().getBytes());
        offset += file.getFilenameWithExtension().getBytes().length;

        byteBuffer.put(offset, ByteBuffer.allocate(4).putInt(file.getBinaryData().length).array());
        offset += 4;

        byteBuffer.put(offset, file.getBinaryData());

        data = byteBuffer.array();
    }

    public static File construct(InputStream inputStream) throws IOException {
        byte[] nameLengthData = inputStream.readNBytes(4);
        int nameLength = ByteBuffer.wrap(nameLengthData).getInt();

        byte[] nameData = inputStream.readNBytes(nameLength);
        String name = new String(nameData);

        byte[] contentLengthData = inputStream.readNBytes(4);
        int contentLength = ByteBuffer.wrap(contentLengthData).getInt();

        byte[] content = inputStream.readNBytes(contentLength);

        return new File(name, content);
    }
}
