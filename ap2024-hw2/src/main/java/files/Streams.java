package files;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Streams {
    /**
     * Read from an InputStream until a quote character (") is found, then read
     * until another quote character is found and return the bytes in between the two quotes.
     * If no quote character was found return null, if only one, return the bytes from the quote to the end of the stream.
     *
     * @param in
     * @return A list containing the bytes between the first occurrence of a quote character and the second.
     */
    public static List<Byte> getQuoted(InputStream in) throws IOException {

        int c;
        List<Byte> res = new ArrayList<>();

        try {
            while ((c = in.read()) != -1) { // while there are bytes to read

                if (c == 34) { // if we reached "
                    break;
                }
            }
            if(c == -1){ // if we reached the and of the file with encounter "
                return null;
            }
            while ((c = in.read()) != -1) { // read the rest of the files after the "
                if (c == 34) { // if we reached "
                    break;
                }
                res.add((byte) c); // add all the bytes
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    /**
     * Read from the input until a specific string is read, return the string read up to (not including) the endMark.
     *
     * @param in      the Reader to read from
     * @param endMark the string indicating to stop reading.
     * @return The string read up to (not including) the endMark (if the endMark is not found, return up to the end of the stream).
     */
    public static String readUntil(Reader in, String endMark) throws IOException {
        StringBuilder result = new StringBuilder();
        int currentChar;

        while ((currentChar = in.read()) != -1) { // whiile we didnt reach the end of the file
            StringBuilder midResult = new StringBuilder();

            if (currentChar == endMark.charAt(0)) { // Check if the current character is the start of the endMark
                boolean match = true;

                for (int i = 1; i < endMark.length(); i++) { // Attempt to match the entire endMark
                    int nextChar = in.read();
                    midResult.append((char) nextChar);

                    if (nextChar == -1 || nextChar != endMark.charAt(i)) { //if we reached the end of the file or a char doesnt match the endMark
                        result.append(endMark.charAt(0));
                        result.append(midResult);
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return result.toString(); // Return the string up to (not including) the endMark
                }
            } else
                result.append((char) currentChar); // The current character is not part of the endMark, append it to the result
        }
        return result.toString();
    }


    /**
     * Copy bytes from input to output, ignoring all occurrences of badByte.
     *
     * @param in
     * @param out
     * @param badByte
     */
    public static void filterOut(InputStream in, OutputStream out, byte badByte) throws IOException {
        int compere = in.read();
        while (compere != -1) { // read until the end of the file
            if ((byte) compere != badByte) { // if this is not the byte you need to filter
                out.write(compere);
            }
            compere = in.read(); // keep reading bytes
        }
    }

    /**
     * Read a 40-bit (unsigned) integer from the stream and return it. The number is represented as five bytes,
     * with the most-significant byte first.
     * If the stream ends before 5 bytes are read, return -1.
     *
     * @param in
     * @return the number read from the stream
     */
    public static long readNumber(InputStream in) throws IOException {
        long result = 0;

        for (int i = 0; i < 5; i++){ // read 5 bytes
            int nextByte = in.read();

            if (nextByte == -1){ // if we couldnt read 5 bytes
                return -1;
            }
            result = result * 256 + nextByte; // build the unsigned integer
        }
        return result;
        }
    }
