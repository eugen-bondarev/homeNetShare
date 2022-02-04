package connection.messages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TextMessage extends Message {
    public TextMessage(String msg) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + msg.length());
        byteBuffer.put(0, ByteBuffer.allocate(4).putInt(msg.length()).array());
        byteBuffer.put(4, msg.getBytes());
        data = byteBuffer.array();
    }

    public static String construct(InputStream inputStream) throws IOException {
        byte[] lengthData = inputStream.readNBytes(4);
        int length = ByteBuffer.wrap(lengthData).getInt();
        byte[] msgData = inputStream.readNBytes(length);
        return new String(msgData);
    }
}
