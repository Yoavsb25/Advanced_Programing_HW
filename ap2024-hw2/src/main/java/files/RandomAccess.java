package files;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccess {
    /**
     * Treat the file as an array of (unsigned) 8-bit values and sort them
     * in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     */
    public static void sortBytes(RandomAccessFile file) throws IOException {
        long fileLength = file.length();

        for (long i = 0; i < fileLength - 1; i++) {
            for (long j = 0; j < fileLength - i - 1; j++) {

                file.seek(j); // resets the pointer to j
                int firstByte = file.read() & 0xFF;
                ;
                int secondByte = file.read() & 0xFF;
                ;

                if (firstByte > secondByte) { // compare two bytes
                    file.seek(j); // resets the pointer to j
                    file.write(secondByte); // writes in position j the smaller byte
                    file.seek(j + 1); // resets the pointer to j + 1
                    file.write(firstByte); // writes in position j + 1 the smaller byte
                }
            }
        }
    }

    /**
     * Treat the file as an array of unsigned 24-bit values (stored MSB first) and sort
     * them in-place using a bubble-sort algorithm.
     * You may not read the whole file into memory!
     *
     * @param file
     * @throws IOException
     */
    public static void sortTriBytes(RandomAccessFile file) throws IOException {
        long fileLength = file.length();

        for (long i = 0; i < fileLength; i += 3) {
            for (long j = 0; j < fileLength - i; j += 3) {

                file.seek(j); // resets the pointer to j

                //reads 3 bytes
                int firstByte = file.read() & 0xFF;
                int secondByte = file.read() & 0xFF;
                int thirdByte = file.read() & 0xFF;

                int firstValue = firstByte * 65536 + secondByte * 256 + thirdByte; // adds all the bytes to one value

                file.seek(j + 3); // resets the pointer to read the next 3 bytes

                //reads 3 bytes
                int firstByteSecondValue = file.read() & 0xFF;
                int secondByteSecondValue = file.read() & 0xFF;
                int thirdByteSecondValue = file.read() & 0xFF;

                int secondValue = firstByteSecondValue * 65536 + secondByteSecondValue * 256 + thirdByteSecondValue; // adds all the bytes to one value


                if (firstValue > secondValue) { // compare two values
                    file.seek(j); // resets the pointer to j

                    // writes the 3 smallest bytes
                    file.write(firstByteSecondValue);
                    file.write(secondByteSecondValue);
                    file.write(thirdByteSecondValue);

                    file.seek(j + 3); // resets the pointer to j + 3

                    // writes the 3 largest bytes
                    file.write(firstByte);
                    file.write(secondByte);
                    file.write(thirdByte);
                }
            }
        }
    }
}
