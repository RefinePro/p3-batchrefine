
package eu.spaziodati.batchrefine.core.spark.storage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class StorageEngine {
    private StorageOperation operation;

   public StorageEngine(StorageOperation operation){
      this.operation = operation;
   }
   
   public URI getSourceURI() throws URISyntaxException {
       return operation.getSourceURI();
   }
    
   public URI getExportURI() throws URISyntaxException {
       return operation.getExportURI();
   }

    public List<String> getLines() throws Exception {
        return operation.getLines();
    }

    public boolean writeDataset() throws Exception {
        return operation.writeDataset();
    }

    public void cleanup() {
        operation.cleanup();
    }
}
