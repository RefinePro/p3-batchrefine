package eu.spaziodati.batchrefine.java;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.runners.Parameterized;

import eu.spaziodati.batchrefine.core.ITransformEngine;
import eu.spaziodati.batchrefine.core.spark.SparkRefine;

public class SparkRefineTest extends EngineTest {

    public SparkRefineTest(String input, String transform, String format,
                           CallType type) {
        super(input, transform, format, type);
        
        // winutil.exe workaround http://qnalist.com/questions/4994960/run-spark-unit-test-on-windows-7
        if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)
            System.setProperty("hadoop.home.dir", "c:\\winutil\\");
    }

    @Override
    protected ITransformEngine engine() throws Exception {
        return new SparkRefine(new Properties());
    }

    @Parameterized.Parameters(name = "{index}: {1}")
    public static Collection<?> parameters() {
        return Arrays.asList(new Object[][]{
                {"osterie", "mass-edit", "csv", CallType.sync},
                {"osterie", "column-addition", "csv", CallType.sync},
                {"osterie", "column-removal", "csv", CallType.sync},
                {"osterie", "column-rename", "csv", CallType.sync},
                {"osterie", "column-move", "csv", CallType.sync},
                {"osterie", "column-split", "csv", CallType.sync},
                {"osterie", "column-addition-by-fetching-urls", "csv", CallType.async},
                {"osterie", "text-transform", "csv", CallType.sync},
                // {"osterie", "rdfize", "rdf", CallType.sync},
                // {"osterie", "rdfize", "turtle", CallType.sync},
                {"high-earners", "multivalued-cell-join", "csv", CallType.sync},
                {"high-earners", "multivalued-cell-split", "csv", CallType.sync},
                {"high-earners", "transpose-columns-into-rows", "csv", CallType.sync}
                // {"high-earners", "save-rdf-schema", "rdf", CallType.sync},
                // {"high-earners", "save-rdf-schema", "turtle", CallType.sync}
		});

    }
}
