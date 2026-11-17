package uk.ac.ncl.ico2s;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.virtualparts.VPRException;
import org.virtualparts.data.Cacher;
import org.virtualparts.data.QueryParameters;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.SVPWriteHandler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	public void testPopulateWithInteractionsInverterDesign_TUs_public() throws Exception {
		try {
			SBOLDocument doc = new SBOLDocument();
			String base = "https://synbiohub.org/public/bsu/";
			doc.setDefaultURIprefix(base);

			// Designs
			String design1 = "BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
			String design2 = "BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";

			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design1), "design1");
			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design2), "design2");

			SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample_TUs_public.xml"));

			TestUtils.addAndAssertInteractions(endpoint, "newDesign", null, doc, "inverterExample_TUs_public_output.xml", 4, "testPopulateWithInteractionsInverterDesign_TUs_public");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testPopulateWithInteractionsNegativeAutoRegulatoryDesign() throws Exception {
		try {
			SBOLDocument doc = SBOLReader.read(new File(TestUtils.getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
			TestUtils.addAndAssertInteractions(endpoint, null, null, doc, "my_NegativeAutoRegulatoryDesign_output.xml", 2, "testPopulateWithInteractionsNegativeAutoRegulatoryDesign");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testPopulateWithInteractionsNegativeAutoRegulatoryeCelloDesign() throws Exception {
		try {
			SBOLDocument doc = new SBOLDocument();
			String base = "https://synbiohub.org/public/bsu/";
			doc.setDefaultURIprefix(base);

			// Designs
			String rbsCdsTerDesign = "BO_27783:rbs;BO_32077:cds;BO_4257:ter";

			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, rbsCdsTerDesign), "rbsCdsTerDesign");
			String design = "BO_27632:prom;rbsCdsTerDesign:eng";
			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design), "NegativeAutoRegulatoryDesign_Cello");
			SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "my_NegativeAutoRegulatoryCelloDesign.xml"));

			TestUtils.addAndAssertInteractions(endpoint, null, null, doc, "my_NegativeAutoRegulatoryCelloDesign_output.xml", 2, "testPopulateWithInteractionsNegativeAutoRegulatoryeCelloDesign");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testPopulateWithInteractionsInverterDesign() throws Exception {
		try {
			SBOLDocument doc = new SBOLDocument();
			String base = "https://synbiohub.org/public/bsu/";
			doc.setDefaultURIprefix(base);

			// Designs
			String design1 = "BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
			String design2 = "BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";

			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design1), "design1");
			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design2), "design2");

			SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample.xml"));

			TestUtils.addAndAssertInteractions(endpoint, null, null, doc, "inverterExample_output.xml", 4, "testPopulateWithInteractionsInverterDesign");

		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testPopulateWithInteractionsInverterDesign_UserProvidedName() throws Exception {
		try {
			SBOLDocument doc = new SBOLDocument();
			String base = "https://synbiohub.org/public/bsu/";
			doc.setDefaultURIprefix(base);

			// Designs
			String design1 = "BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
			String design2 = "BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";

			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design1), "design1");
			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design2), "design2");

			SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample_UserProvidedName.xml"));

			TestUtils.addAndAssertInteractions(endpoint, "newDesign", null, doc, "inverterExample_UserProvidedName_output.xml", 4, "testPopulateWithInteractionsInverterDesign_UserProvidedName");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testPopulateWithInteractionsInverterDesign_CollectionFilter() throws Exception {
		try {
			SBOLDocument doc = new SBOLDocument();
			String base = "https://synbiohub.org/public/bsu/";
			doc.setDefaultURIprefix(base);

			// Designs
			String design1 = "BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
			String design2 = "BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";

			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design1), "design1");
			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design2), "design2");

			SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "InverterDesign_CollectionFilter.xml"));

			List<URI> collections = new ArrayList<URI>();
			collections.add(new URI("https://synbiohub.org/public/bsu/bsu_collection/1"));

			QueryParameters params = new QueryParameters();
			params.setCollectionURIs(collections);
			TestUtils.addAndAssertInteractions(endpoint, "newDesign", params, doc, "inverterDesign_CollectionFilter_output.xml", 4, "testPopulateWithInteractionsInverterDesign_CollectionFilter");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testPopulateWithInteractionsInverterSingleDesign_CollectionFilter() throws Exception {
		try {
			SBOLDocument doc = new SBOLDocument();
			String base = "https://synbiohub.org/public/bsu/";
			doc.setDefaultURIprefix(base);

			// Designs
			String design1 = "BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
			String design2 = "BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";
			String design = design1 + ";" + design2;

			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design), "design");
			SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterSingleDesign_CollectionFilter.xml"));
			List<URI> collections = new ArrayList<URI>();
			collections.add(new URI("https://synbiohub.org/public/bsu/bsu_collection/1"));

			QueryParameters params = new QueryParameters();
			params.setCollectionURIs(collections);
			TestUtils.addAndAssertInteractions(endpoint, "newDesign", params, doc, "InverterSingleDesign_CollectionFilter_output.xml", 4, "testPopulateWithInteractionsInverterSingleDesign_CollectionFilter");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testCircuit08() throws Exception {
		try {
			SBOLDocument doc = SBOLReader.read(TestUtils.getOutputDir() + "circuit_0x08_input.xml");
			List<URI> collections = new ArrayList<URI>();
			collections.add(new URI("https://synbiohub.org/public/Eco1C1G1T1/Eco1C1G1T1_collection/1"));
			QueryParameters params = new QueryParameters();
			params.setCollectionURIs(collections);
			TestUtils.addAndAssertInteractions(endpoint, "newDesign", params, doc, "circuit_0x08_output.xml", 29, "testCircuit08");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testCache() throws Exception {
		try {
			SBOLDocument doc = SBOLReader.read(TestUtils.getOutputDir() + "circuit_0x08_input.xml");
			Cacher cacher = new Cacher();
			cacher.putInCache(doc, 1);
			SBOLDocument doc2 = cacher.retrieveFromCache(1);
			if (doc2 == null) {
				throw new VPRException("Cache does not work!");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void testPopulateWithInteractionsInverterSingleDesign() throws Exception {
		try {
			SBOLDocument doc = new SBOLDocument();
			String base = "https://synbiohub.org/public/bsu/";
			doc.setDefaultURIprefix(base);

			// Designs
			String design1 = "BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
			String design2 = "BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";
			String design = design1 + ";" + design2;

			SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design), "design");
			SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterSingleDesignExample.xml"));
			TestUtils.addAndAssertInteractions(endpoint, null, null, doc, "inverterSingleDesignExample_output.xml", 4, "testPopulateWithInteractionsInverterSingleDesign");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String endpoint = "https://synbiohub.org/sparql";

}
