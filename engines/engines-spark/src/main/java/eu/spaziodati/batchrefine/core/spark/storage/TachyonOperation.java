package eu.spaziodati.batchrefine.core.spark.storage;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import tachyon.TachyonURI;
import tachyon.client.InStream;
import tachyon.client.OutStream;
import tachyon.client.ReadType;
import tachyon.client.TachyonFS;
import tachyon.client.TachyonFile;
import tachyon.client.WriteType;
import tachyon.conf.TachyonConf;

public class TachyonOperation implements StorageOperation {

    private URI srcURI = null;
    private URI exportURI = null;
    private final Integer v1;
    private final Iterator<String> v2;
    private final Properties exporterProperites;
    private final String fHeader;

    private final TachyonFS tachyonClient;
    private static final String TACHYON_MASTER_ADDRESS = "TACHYON_MASTER_ADDRESS";

    public TachyonOperation(Integer v1, Iterator<String> v2, Properties fProperites, String fHeader) {
        this.v1 = v1;
        this.v2 = v2;
        this.exporterProperites = fProperites;
        this.fHeader = fHeader;

        tachyonClient = buildTachyonClient();
    }

    private URI toURI(TachyonURI uri) throws URISyntaxException {
        return new URI(uri.getScheme(), uri.getHost(), uri.getPath(), "");
    }

    private TachyonURI toTachyonURI(URI uri) throws URISyntaxException {
        return new TachyonURI(uri.getPath());
    }

    @Override
    public URI getSourceURI() throws URISyntaxException {
        if (srcURI == null) {        
            TachyonURI uri = new TachyonURI("/tmp/tmp" + v1
                    + exporterProperites.getProperty("fileanme", "inputfile") + ".csv");
            srcURI = toURI(uri);
        }
        
        return srcURI;
    }

    @Override
    public URI getExportURI() throws URISyntaxException {
        if (exportURI == null) {   
            TachyonURI uri = new TachyonURI("/tmp/tmp" + v1 + "_out.tmp");
            exportURI = toURI(uri);
        }
        
        return exportURI;
    }
    
    /**
     * TODO: work with importer
     * @return
     * @throws Exception 
     */
    @Override
    public List<String> getLines() throws Exception {
        List<String> listLines = new ArrayList<>();

        TachyonFile file = tachyonClient.getFile(toTachyonURI(getSourceURI()), true);

        InStream is = file.getInStream(ReadType.NO_CACHE);
        int size = is.available();
        ByteBuffer buf = ByteBuffer.allocate(size);
        is.read(buf.array());
        buf.order(ByteOrder.nativeOrder());

        try (BufferedReader bufferedReader = byteToReader(buf.array())) {
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                listLines.add(s + "\n");
            }
        }
        
        return listLines;
    }

    private BufferedReader byteToReader(byte[] content) {
        InputStream is = new ByteArrayInputStream(content);
        BufferedReader bfReader = new BufferedReader(new InputStreamReader(is));

        return bfReader;
    }

    @Override
    public boolean writeDataset() throws Exception {
        TachyonFile file = tachyonClient.getFile(toTachyonURI(getExportURI()), true);

        try (OutStream os = file.getOutStream(WriteType.TRY_CACHE)) {
            if (v1 != 0) {
                os.write((fHeader + "\n").getBytes());
            }
            
            while (v2.hasNext()) {
                os.write((v2.next() + "\n").getBytes());
            }
        }

        return true;
    }

    @Override
    public void cleanup() {

    }

    private TachyonFS buildTachyonClient() {
        // TODO: externalize the master URI
        TachyonURI mMasterLocation = new TachyonURI(System.getProperty(TACHYON_MASTER_ADDRESS));
        TachyonFS tachyonClient = null;

        try {
            tachyonClient = TachyonFS.get(mMasterLocation, new TachyonConf());
        } catch (IOException ex) {
            Logger.getLogger(TachyonOperation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tachyonClient; 
    }

}
