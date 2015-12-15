
package eu.spaziodati.batchrefine.core.spark.storage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public interface StorageOperation {
    
    public URI getSourceURI() throws URISyntaxException;
    
    public URI getExportURI() throws URISyntaxException;

    public List<String> getLines() throws Exception;

    public boolean writeDataset() throws Exception;

    public void cleanup();
    
}
