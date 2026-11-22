package uk.ac.ncl.ico2s;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.virtualparts.data.QueryParameters;
import org.virtualparts.data2.SBOLInteractionAdder_GeneCentric;
import org.virtualparts.sbol.SBOLHandler;

public class TestUtils {

	/*
	 * Optional login token for the SPARQL endpoint (synbiohub.org requires one). Tests read it
	 * from the SYNBIOHUB_TOKEN environment variable, or from -Dsynbiohub.token=... on the mvn
	 * command line. Without a token, queries are sent without the X-authorization header.
	 *
	 * To get a token
	 *   curl -X POST -H "Accept: text/plain" --data-urlencode "email=EMAIL" --data-urlencode "password=PWD" https://synbiohub.org/login
	 *
	 * Set the environment variable:
	 *   macOS/Linux: add  export SYNBIOHUB_TOKEN='TOKEN'  to ~/.zshrc (or ~/.bashrc)
	 *   Windows:     setx SYNBIOHUB_TOKEN "TOKEN"   (then restart the terminal and VS Code)
	 */
	public static final String TOKEN_PROPERTY = "synbiohub.token";
	public static final String TOKEN_ENV = "SYNBIOHUB_TOKEN";

	/**
	 * Returns the token from the synbiohub.token system property (mvn -D) or, if not set,
	 * from the SYNBIOHUB_TOKEN environment variable. Returns null if neither is set.
	 */
	public static String getToken()
	{
		String token=System.getProperty(TOKEN_PROPERTY);
		if (token==null || token.isBlank())
		{
			token=System.getenv(TOKEN_ENV);
		}
		return (token==null || token.isBlank()) ? null : token.trim();
	}
	 public static String getSVPDesign(String base, String design)
	    {
	    	String[] parts=design.split(";");
	    	StringBuilder builder=new StringBuilder();
	    	for (String part:parts)
	    	{
	    		String[] details=part.split(":");
	    		if (builder.length()>0)
	    		{
	    			builder.append(";");
	    		}
	    		builder.append(String.format("<%s%s>:%s", base, details[0],details[1])) ;
	    	}
	    	return builder.toString();		
	    }
	 

	    public static String getOutputDir()
	    {
	    	Path currentRelativePath = Paths.get("");
		    String homeDirectory = currentRelativePath.toAbsolutePath().getParent().toString();
		    
	    	return homeDirectory + File.separator  + "data" + File.separator + "sbol2_interactions" + File.separator;

	    }

	    /** Returns the total number of interactions in all module definitions of the document. */
	    public static int getInteractionCount(SBOLDocument doc)
	    {
	    	int count=0;
	    	for (ModuleDefinition moduleDef:doc.getModuleDefinitions())
	    	{
	    		count+=moduleDef.getInteractions().size();
	    	}
	    	return count;
	    }

	    /**
	     * Prints the number of interactions added to the document since interactionsBefore was
	     * counted, and fails if none were added. Returns the number added.
	     */
	    public static int assertInteractions(SBOLDocument doc, int interactionsBefore, String testName)
	    {
	    	int interactionsAdded=getInteractionCount(doc) - interactionsBefore;
	    	System.out.println(testName + ": added " + interactionsAdded + " interactions.");
	    	Assert.assertTrue(testName + ": no interactions were added", interactionsAdded > 0);
	    	return interactionsAdded;
	    }

	    /**
	     * Adds interactions to the document from the given endpoint using the user's token,
	     * writes the result to outputFileName in the output directory, and fails unless
	     * exactly expectedInteractions interactions were added.
	     */
	    public static void addAndAssertInteractions(String endpoint, String rootModuleId, QueryParameters params,
	    		SBOLDocument doc, String outputFileName, int expectedInteractions, String testName) throws Exception
	    {
	    	SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint), getToken(), rootModuleId, params);
	    	int interactionsBefore=getInteractionCount(doc);
	    	interactionAdder.addInteractions(doc);
	    	SBOLHandler.write(doc, new File(getOutputDir() + outputFileName));
	    	int interactionsAdded=assertInteractions(doc, interactionsBefore, testName);
	    	Assert.assertEquals(testName + ": unexpected number of interactions added", expectedInteractions, interactionsAdded);
	    }
}
