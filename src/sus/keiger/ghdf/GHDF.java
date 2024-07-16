package sus.keiger.ghdf;

import java.nio.ByteOrder;

final class GHDF
{
    // Static fields.
    public static final byte[] SIGNATURE = new byte[] { (byte)102, (byte)37, (byte)143, (byte)181, (byte)3,
            (byte)205, (byte)123, (byte)185, (byte)148, (byte)157, (byte)98,
            (byte)177, (byte)178, (byte) 151, (byte)43, (byte)170 };
    
    public static final ByteOrder ENDIANNESS = ByteOrder.LITTLE_ENDIAN;

    public static final String EXTENSION = ".ghdf";
}