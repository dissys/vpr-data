package org.virtualparts.data;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTPBuilder;
import org.apache.jena.sparql.exec.http.QuerySendMode;
import org.virtualparts.VPRException;
import org.virtualparts.rdf.RDFHandler;

public class TripleStoreHandler {
	private String endPointURL;
	private String token;

	public TripleStoreHandler(String endPointURL, String token) {
		this.endPointURL = endPointURL;
		this.token = token;
	}

	/**
	 * Creates an HTTP query execution for the endpoint. If a token is provided, it is sent
	 * in the X-authorization header so that private stores can be queried after login.
	 */
	private QueryExecution createQueryExecution(String sparqlQuery, Syntax syntax, String token) {
		// Long queries are sent as an HTML form POST: SynBioHub does not accept the query as a
		// POST body (application/sparql-query), which is Jena's default for queries over the GET limit.
		QueryExecutionHTTPBuilder builder = QueryExecutionHTTP.service(this.endPointURL)
				.sendMode(QuerySendMode.asGetWithLimitForm);
		if (token != null && !token.isEmpty()) {
			builder.httpHeader("X-authorization", token);
		}
		return syntax == null ? builder.query(sparqlQuery).build() : builder.query(sparqlQuery, syntax).build();
	}
	    
	public ResultSet executeSparql(String sparqlQuery, String token)  throws VPRException{
		try (QueryExecution qexec = createQueryExecution(sparqlQuery, null, token))
		{
			// Copy the results into memory before the HTTP connection is closed.
			return ResultSetFactory.copyResults(qexec.execSelect());
		}
		catch (Exception e)
		{
			throw new VPRException(e.getMessage(),e);
		}
	}
	
	public String executeSparqlWithJson(String sparqlQuery, String token)  throws VPRException{
		try
		{
			ResultSet results = executeSparql(sparqlQuery, token);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(outputStream, results);
			return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
		}
		catch (Exception e)
		{
			throw new VPRException(e.getMessage(),e);
		}
	}
	
	public String executeConstructSparql(String sparqlQuery, String token) throws VPRException {
		try
		{
			Model model=executeConstructSparqlAsModel(sparqlQuery, token);
			RDFHandler rdfHandler=new RDFHandler(model);
			// Plain (non-nested) RDF/XML, as libSBOLj reads it reliably
			return rdfHandler.getRdfString(getDefaultFormat(), null);
		}
		catch (Exception e)
		{
			throw new VPRException("Could not execute the CONSTRUCT query. " + e.getMessage(),e);
		}		
	}
	
	public Model executeConstructSparqlAsModel(String sparqlQuery, String token) throws VPRException {
		try (QueryExecution qexec = createQueryExecution(sparqlQuery, Syntax.syntaxSPARQL_11, token))
		{
			return qexec.execConstruct();
		}
		catch (Exception e)
		{
			throw new VPRException("Could not execute the CONSTRUCT query. " + e.getMessage() + "\nQuery:\n" + sparqlQuery, e);
		}
	}
	
	/*public String getRdfString(Model model, String format, Resource[] topLevelResources)
			throws VPRException {
		String rdfData = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (format == null || format.length() == 0) {
			format = getDefaultFormat();
		}
		try {
			RDFWriter writer = model.getWriter(format);
			// fasterWriter.setProperty("allowBadURIs","true");
			// fasterWriter.setProperty("relativeURIs","");
			writer.setProperty("tab", "3");
			if (topLevelResources != null && topLevelResources.length > 0) {
				writer.setProperty("prettyTypes", topLevelResources);
			}
			writer.write(model, stream, null);
			rdfData = new String(stream.toString());
		} finally {
			if (stream != null) {
				try
				{
					stream.close();
				}
				catch(Exception e){}
				stream = null;
			}
		}
		return rdfData;
	}*/
	
	public static String getDefaultFormat() {
		//return "RDF/XML-ABBREV";
		return "RDF/XML";
		
	}
	
	public String getSparqlQuery(String fileName) throws VPRException
    {
	 
    	/*ClassLoader classLoader = this.getClass().getClassLoader();
    	File file=new File (classLoader.getResource(fileName).getFile());
    	String sparql=null;
    	try
    	{
    		sparql=FileUtils.readFileToString(file);
    	}
    	catch (IOException ex)
    	{
    		throw new WebApplicationException(ex.getMessage(),ex);
    	
    	}*/
		try
		{
			InputStream stream= new TripleStoreHandler("","").getClass().getClassLoader().getResourceAsStream(fileName);
	    	StringWriter writer = new StringWriter();
	    	IOUtils.copy(stream, writer);
	    	String sparql = writer.toString();
	    	return sparql;
		}
    	catch (Exception e)
    	{
    		throw new VPRException("Could not find the resource " + fileName + "." + e.getMessage(), e);
    	}
    }
	
	public static List<URI> getUris(String data,String separator)
	{
		List<String> items=getItems(data, separator);
		List<URI> uriList=new ArrayList<URI>();
		for (String item:items)
		{
			uriList.add(URI.create(item));
		}
		return uriList;
	}
	
	public static List<String> getItems(String data,String separator)
	{
		List<String> itemList=new ArrayList<String>();
		if (data!=null && data.length()>0)
		{
			String[] items=data.split(separator);
			itemList=Arrays.asList(items);
		}
		return itemList;
	}
	
}
