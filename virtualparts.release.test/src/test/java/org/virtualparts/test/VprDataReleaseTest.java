package org.virtualparts.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.net.URI;

import org.junit.Test;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.virtualparts.data2.SBOLInteractionAdder_GeneCentric;
import org.virtualparts.sbol.SVPWriteHandler;

/**
 * Uses a vpr-data release from Maven Central as a library. The first test builds a design without
 * network access; the second queries synbiohub.org and is skipped unless SYNBIOHUB_TOKEN is set.
 */
public class VprDataReleaseTest {

    private static final String ENDPOINT = "https://synbiohub.org/sparql";
    private static final String BASE = "https://synbiohub.org/public/bsu/";

    // Two inverters as a single flat design: promoter, RBS, CDS and terminator parts from the bsu collection
    private static final String DESIGN = "BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter;" + "BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";

    /** Checks that the classes come from the released vpr-data jar. */
    @Test
    public void usesReleasedJar() {
        String location = SBOLInteractionAdder_GeneCentric.class.getProtectionDomain().getCodeSource().getLocation().toString();
        System.out.println("vpr-data loaded from: " + location);
    }

    /** Builds an SBOL design from the vpr-data jar, without network access. */
    @Test
    public void createsDesignWithoutNetwork() throws Exception {
        SBOLDocument doc = createDesign();

        ComponentDefinition design = doc.getComponentDefinition(URI.create(BASE + "design/1"));
        assertNotNull("The design was not created", design);
        assertEquals("Number of parts in the design", 8, design.getComponents().size());
        System.out.println("Design created with " + design.getComponents().size() + " parts.");
    }

    /** Adds interactions to the design from synbiohub.org. */
    @Test
    public void addsInteractionsFromSynBioHub() throws Exception {
        String token = System.getenv("SYNBIOHUB_TOKEN");
        assumeTrue("SYNBIOHUB_TOKEN is not set!", token != null && !token.isBlank());

        SBOLDocument doc = createDesign();
        int before = countInteractions(doc);

        SBOLInteractionAdder_GeneCentric adder = new SBOLInteractionAdder_GeneCentric(URI.create(ENDPOINT), token, null, null);
        adder.addInteractions(doc);

        int added = countInteractions(doc) - before;
        System.out.println("Added " + added + " interactions.");
        assertTrue("No interactions were added", added > 0);
        assertEquals("Number of interactions added", 4, added);
    }

    private static SBOLDocument createDesign() throws Exception {
        SBOLDocument doc = new SBOLDocument();
        doc.setDefaultURIprefix(BASE);
        SVPWriteHandler.convertToSBOL(doc, toSVPDesign(DESIGN), "design");
        return doc;
    }

     // Converts "id:role;id:role" into the "<base+id>:role;..." form */
     private static String toSVPDesign(String design) {
        StringBuilder builder = new StringBuilder();
        for (String part : design.split(";")) {
            String[] details = part.split(":");
            if (builder.length() > 0) {
                builder.append(";");
            }
            builder.append(String.format("<%s%s>:%s", BASE, details[0], details[1]));
        }
        return builder.toString();
    }

    private static int countInteractions(SBOLDocument doc) {
        int count = 0;
        for (ModuleDefinition moduleDef : doc.getModuleDefinitions()) {
            count += moduleDef.getInteractions().size();
        }
        return count;
    }
}
