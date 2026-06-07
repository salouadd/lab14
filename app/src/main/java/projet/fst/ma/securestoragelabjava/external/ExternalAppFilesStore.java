package projet.fst.ma.securestoragelabjava.external;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ExternalAppFilesStore {

    private ExternalAppFilesStore() {}

    public static String write(Context context, String fileName, String content) throws Exception {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) return null;

        File file = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return file.getAbsolutePath();
    }

    public static String read(Context context, String fileName) throws Exception {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) return null;

        File file = new File(dir, fileName);
        if (!file.exists()) return null;
        try (FileInputStream fis = new FileInputStream(file)) {
            return new String(readAllBytes(fis), StandardCharsets.UTF_8);
        }
    }

    private static byte[] readAllBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static boolean delete(Context context, String fileName) {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) return false;

        File file = new File(dir, fileName);
        return file.delete();
    }
}
