package connection.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TextMessage extends Message {
    public TextMessage(String msg) {
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + msgBytes.length);
        byteBuffer.put(0, ByteBuffer.allocate(4).putInt(msgBytes.length).array());
        byteBuffer.put(4, msgBytes);
        data = byteBuffer.array();
    }

    public static String construct(InputStream inputStream) throws IOException {
        byte[] lengthData = inputStream.readNBytes(4);
        int length = ByteBuffer.wrap(lengthData).getInt();
        byte[] msgData = inputStream.readNBytes(length);
        return new String(msgData, StandardCharsets.UTF_8);
    }
}
