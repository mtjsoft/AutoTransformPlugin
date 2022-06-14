package cn.mtjsoft.www.plugin;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public final class MappingPrinter {

    private final File mMappingFile;
    private BufferedWriter writer;

    /* package */
    public MappingPrinter(File mappingFile) {
        this.mMappingFile = mappingFile;

        if (!mappingFile.getParentFile().exists()) {
            mappingFile.getParentFile().mkdirs();
            try {
                mappingFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete() {
        if (mMappingFile.exists()) {
            mMappingFile.delete();
        }
    }

    public void log(String log) {
        try {
            if (writer == null) {
                FileOutputStream outputStream = new FileOutputStream(mMappingFile, true);
                OutputStreamWriter outputStream1 = new OutputStreamWriter(outputStream);
                writer = new BufferedWriter(outputStream1);
            }
            writer.write(log);
            writer.flush();
            writer.newLine();
            close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer = null;
    }

}
