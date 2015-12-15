
package eu.spaziodati.batchrefine.core.spark.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;

public class FileSystemOperation implements StorageOperation {
    private URI srcURI;
    private URI exportURI;
    private final Integer v1;
    private final Iterator<String> v2;
    private final Properties fProperites;
    private final String fHeader;
    
    public FileSystemOperation(Integer v1, Iterator<String> v2, Properties fProperites, String fHeader) {
        this.v1 = v1;
        this.v2= v2;
        this.fProperites = fProperites;
        this.fHeader = fHeader;      
    }
    
    @Override
    public URI getSourceURI() {
        File tmpFile = new File("/tmp/tmp" + v1
                + fProperites.getProperty("fileanme", "inputfile") + ".csv");
        return tmpFile.toURI();

    }

    @Override
    public URI getExportURI() {
        File tmpOut = new File("/tmp/tmp" + v1 + "_out.tmp");
        
        return tmpOut.toURI();
    }

    @Override
    public List<String> getLines() throws Exception {
        File exportFile = new File(exportURI);
        return FileUtils.readLines(exportFile);
    }

    @Override
    public boolean writeDataset() throws Exception {
        File tmpFile = new File(srcURI);
        BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(tmpFile));
        if (v1 != 0) {
                out.write((fHeader + "\n").getBytes());
        }

        while (v2.hasNext()) {
                out.write((v2.next() + "\n").getBytes());
        }
        out.close();
        
        return true;
    }

    @Override
    public void cleanup() {
        FileUtils.deleteQuietly(new File(srcURI));
	FileUtils.deleteQuietly(new File(exportURI));
    }
    
}
