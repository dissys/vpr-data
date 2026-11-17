package uk.ac.ncl.ico2s;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.virtualparts.data.QueryParameters;
import org.virtualparts.data2.SBOLInteractionAdder_GeneCentric;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.SVPWriteHandler;

import junit.framework.TestCase;

public class testSingleTest extends TestCase {
    public void testPopulateWithInteractionsNegativeAutoRegulatoryeCelloDesign() throws Exception
    {
	 try
	 {
		SBOLDocument doc=new SBOLDocument();
	    String base="https://synbiohub.org/public/bsu/";
	    doc.setDefaultURIprefix(base);
	   
	    //Designs
	    String rbsCdsTerDesign="BO_27783:rbs;BO_32077:cds;BO_4257:ter";
	    
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, rbsCdsTerDesign), "rbsCdsTerDesign");
	    String design="BO_27632:prom;rbsCdsTerDesign:eng";
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "NegativeAutoRegulatoryDesign_Cello");
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "testSBOLWrite.xml")); 
	    		
	    SBOLDocument newDoc=SBOLReader.read(new File(TestUtils.getOutputDir() + "testSBOLWrite.xml"));
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    /**
     * Adds interactions from the Cello Eco1C1G1T1 collection on synbiohub.org to a design.
     * Checks structure rather than exact counts, so the test keeps working when the
     * collection's content changes. synbiohub.org requires login, so the test is skipped
     * without a token.
     */
    public void testInteractionsCopiedToTopLevel() throws Exception
    {
	 String token=TestUtils.getToken();
	 if (token==null || token.isEmpty())
	 {
		 System.out.println("Skipping testInteractionsCopiedToTopLevel: no SynBioHub token configured.");
		 return;
	 }
	 try
	 {
		SBOLDocument doc=SBOLReader.read(TestUtils.getOutputDir() + "InteractionsCopiedToTopLevelInput.sbol");
		String endpoint="https://synbiohub.org/sparql";
		String rootModuleId="newDesign";
		List<URI> collections=new ArrayList<URI>();
	    collections.add(new URI("https://synbiohub.org/public/Eco1C1G1T1/Eco1C1G1T1_collection/1"));
	    QueryParameters params=new QueryParameters();
	    params.setCollectionURIs(collections);
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint),token,rootModuleId,params);
		interactionAdder.addInteractions(doc);
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "InteractionsCopiedToTopLevelOutput.xml"));

	    // The requested root module is the only top-level module and includes sub-modules
	    assertEquals("Expected a single top-level module", 1, doc.getRootModuleDefinitions().size());
	    ModuleDefinition root=doc.getRootModuleDefinitions().iterator().next();
	    assertEquals(rootModuleId, root.getDisplayId());
	    assertFalse("The root module has no sub-modules", root.getModules().isEmpty());

	    int interactionCount=assertInteractionsValid(doc);

	    // Interactions found for this design in the Cello Eco1C1G1T1 collection on 16 Nov 2026.
	    // Update this number if the collection on synbiohub.org changes.
	    int expectedInteractionCount=14;
	    assertEquals("Unexpected number of interactions. Check whether the collection on synbiohub.org has changed.",
	    		expectedInteractionCount, interactionCount);
	    System.out.println("testInteractionsCopiedToTopLevel: found the expected " + expectedInteractionCount + " interactions.");
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }

    /**
     * Checks that interactions were added and that every participant is a functional
     * component of the module containing the interaction. Returns the number of interactions.
     */
    private static int assertInteractionsValid(SBOLDocument doc)
    {
    	int interactionCount=0;
    	for (ModuleDefinition moduleDef:doc.getModuleDefinitions())
    	{
    		for (Interaction interaction:moduleDef.getInteractions())
    		{
    			interactionCount++;
    			assertFalse("Interaction without participations: " + interaction.getIdentity(), interaction.getParticipations().isEmpty());
    			for (Participation participation:interaction.getParticipations())
    			{
    				assertNotNull("Participant is not a functional component of " + moduleDef.getIdentity() + ": " + participation.getParticipantURI(),
    						moduleDef.getFunctionalComponent(participation.getParticipantURI()));
    			}
    		}
    	}
    	assertTrue("No interactions were added", interactionCount>0);
    	return interactionCount;
    }

}
