package uk.ac.ncl.ico2s;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;
import org.apache.jena.sparql.exec.http.QuerySendMode;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.virtualparts.VPRException;
import org.virtualparts.data.Cacher;
import org.virtualparts.data.QueryParameters;
import org.virtualparts.data.SBOLInteractionAdder_GeneCentric;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.SVPWriteHandler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ConstructTest  extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ConstructTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ConstructTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testConstruct()
    {
       // 2️⃣ Your CONSTRUCT query
       String sparqlQuery = "CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o .} LIMIT 1";

	   String sparqlQuery2="PREFIX sbol2: <http://sbols.org/v2#>\r\n" + //
				"PREFIX biopax: <http://www.biopax.org/release/biopax-level3.owl#>\r\n" + //
				"PREFIX dcterms: <http://purl.org/dc/terms/>\r\n" + //
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + //
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n" + //
				"PREFIX ncbi: <http://www.ncbi.nlm.nih.gov#>\r\n" + //
				"PREFIX soIdentifiers: <http://identifiers.org/so/>\r\n" + //
				"PREFIX prov: <http://www.w3.org/ns/prov#>\r\n" + //
				"\r\n" + //
				"\r\n" + //
				"CONSTRUCT\r\n" + //
				"{\r\n" + //
				"  ?ModuleDefinition a sbol2:ModuleDefinition ;\r\n" + //
				"    sbol2:functionalComponent ?FunctionalComponent ;\r\n" + //
				"    sbol2:interaction ?Interaction .\r\n" + //
				"\r\n" + //
				"  ?Interaction \r\n" + //
				"    sbol2:participation ?Participation ;\r\n" + //
				"    ?IntereactionProperty ?IntereactionPropertyValue .\r\n" + //
				"\r\n" + //
				"  ?Participation ?ParticipationProperty ?ParticipationPropertyValue ;\r\n" + //
				"    sbol2:participant ?FunctionalComponent .\r\n" + //
				"  \r\n" + //
				"  ?FunctionalComponent a sbol2:FunctionalComponent ;\r\n" + //
				"    sbol2:definition ?ComponentDefinition ;\r\n" + //
				"    sbol2:access ?Access ;\r\n" + //
				"    sbol2:direction ?Direction ;\r\n" + //
				"    sbol2:mapsTo ?MapsTo .\r\n" + //
				"}\r\n" + //
				"WHERE\r\n" + //
				"{\r\n" + //
				"  {\r\n" + //
				"    ?Interaction a sbol2:Interaction ;\r\n" + //
				"      sbol2:participation ?Participation.       \r\n" + //
				"  \r\n" + //
				"    ?Participation a sbol2:Participation ;\r\n" + //
				"      sbol2:participant ?FunctionalComponent ;\r\n" + //
				"      ?ParticipationProperty ?ParticipationPropertyValue .\r\n" + //
				"  \r\n" + //
				"    ?FunctionalComponent a sbol2:FunctionalComponent ;\r\n" + //
				"      sbol2:definition ?ComponentDefinition .\r\n" + //
				"    \r\n" + //
				"  OPTIONAL {?FunctionalComponent sbol2:access ?Access .}\r\n" + //
				"  OPTIONAL {?FunctionalComponent sbol2:direction ?Direction .}\r\n" + //
				"  OPTIONAL {?FunctionalComponent sbol2:mapsTo ?MapsTo .}  \r\n" + //
				"        \r\n" + //
				"    ?ModuleDefinition a sbol2:ModuleDefinition ;\r\n" + //
				"      sbol2:interaction ?Interaction .\r\n" + //
				"           \r\n" + //
				"    \r\n" + //
				"    FILTER (?Interaction = <https://synbiohub.org/public/bsu/module_BO_10845_represses_BO_27632/BO_10845_represses_BO_27632/1>)\r\n" + //
				"    FILTER (?ParticipationProperty != sbol2:participant)\r\n" + //
				"    \r\n" + //
				"  }  \r\n" + //
				"  UNION  \r\n" + //
				"  {  \r\n" + //
				"    ?Interaction a sbol2:Interaction ;\r\n" + //
				"      ?IntereactionProperty ?IntereactionPropertyValue .      \r\n" + //
				"    \r\n" + //
				"    FILTER (?Interaction = <https://synbiohub.org/public/bsu/module_BO_10845_represses_BO_27632/BO_10845_represses_BO_27632/1>)\r\n" + //
				"    FILTER (?IntereactionProperty != sbol2:participation)  \r\n" + //
				"  } \r\n" + //
				"}\r\n" + //
				"\r\n" ;
				

				Query query = QueryFactory.create(sparqlQuery2);

try (QueryExecution qexec =
     QueryExecutionHTTP.service(endpoint)
                        .query(query).sendMode(QuerySendMode.asPostForm)                    
                        .build()) {

    Model model = qexec.execConstruct();
    model.write(System.out, "TURTLE");
}


		/* Did not work
		Query query = QueryFactory.create(sparqlQuery2);
		try (QueryExecution qexec =
        QueryExecutionHTTP
            .service(endpoint)
            .query(sparqlQuery2)
			.postQuery()
            .build()) {
			Model model = qexec.execConstruct();
			model.write(System.out, "TURTLE");
		}*/


/* 			
		Query query = QueryFactory.create(sparqlQuery2);

		try (QueryExecution qexec =
				QueryExecutionHTTP.service(endpoint)
								.query(query)
								.build()) {

			Model model = qexec.execConstruct();
			model.write(System.out, "TURTLE");
		}
*/
		
		/* 

        // 4️⃣ Execute the query using try-with-resources
        try (QueryExecutionHTTP qexec = QueryExecutionHTTP.service(endpoint, sparqlQuery2)) {
                   	
            Model model = qexec.execConstruct();

            // 6️⃣ Print the model in Turtle format
            model.write(System.out, "TURTLE");
        }
		*/
    }
 
   
    
        
    public static String endpoint="https://synbiohub.org/sparql";
    
    
}
