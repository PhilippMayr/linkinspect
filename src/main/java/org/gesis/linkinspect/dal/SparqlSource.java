/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.dal;

import java.net.URL;
import javafx.collections.ObservableList;
import org.gesis.linkinspect.model.ResourceProperty;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

/**
 * Populates a given list with data from a SPARQL endpoint
 */
public class SparqlSource {

    private URL endpoint = null;
    private ObservableList observableList = null;

    public SparqlSource(URL endpoint, ObservableList list) {
        this.endpoint = endpoint;
        this.observableList = list;
    }

    /**
     * Request a given resource from the SPARQL endpoint and populate the list with it.
     * @param resource The resource to request.
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException 
     */
    @SuppressWarnings("unchecked")
    public void requestResource(String resource) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        observableList.clear();
        
        Repository repo = new SPARQLRepository(endpoint.toString());
        repo.initialize();
        RepositoryConnection con = repo.getConnection();
        String request = "SELECT ?p ?o WHERE { <" + resource + "> ?p ?o }";
        TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, request);
        query.setIncludeInferred(false);
        TupleQueryResult result = query.evaluate();
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Value predicate = bindingSet.getValue("p");
            Value object = bindingSet.getValue("o");
            ResourceProperty rp = new ResourceProperty(new URIImpl(predicate.stringValue()), object, true, endpoint.toString());
            observableList.add(rp);
        }
        addInverse(resource);
        result.close();
        con.close();
    }

    /**
     * Adds resource that reference the given resource to the observable list.
     * @param resource
     * @return 
     */
    @SuppressWarnings("unchecked")
    public void addInverse(String resource) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
        Repository repo = new SPARQLRepository(endpoint.toString());
        repo.initialize();
        RepositoryConnection con = repo.getConnection();
        String request = "SELECT ?s ?p WHERE {  ?s ?p <" + resource + ">}";
        TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, request);
        query.setIncludeInferred(false);
        TupleQueryResult result = query.evaluate();
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Value predicate = bindingSet.getValue("p");
            Value subject = bindingSet.getValue("s");
            ResourceProperty rp = new ResourceProperty(new URIImpl(predicate.stringValue()), subject, false, endpoint.toString());
            observableList.add(rp);
        }
        result.close();
        con.close();
    }

    /**
     * Connects to a SPARQL endpoint and closes the connection immediately.
     * @param sparqlEp
     * @return True, if a connection was made.
     */
    public static boolean checkConnectivity(String sparqlEp) {
        Repository repo = new SPARQLRepository(sparqlEp);
        try {
            repo.initialize();
            RepositoryConnection con = repo.getConnection();
            con.close();
        } catch (OpenRDFException e) {
            return false;
        }
        return true;
    }
    
    
    public static boolean isPresent(String sparqlEp, String resource) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
        Repository repo = new SPARQLRepository(sparqlEp);
        repo.initialize();
        RepositoryConnection con = repo.getConnection();
        String request = "SELECT COUNT (?p) AS ?cnt WHERE { <" + resource + "> ?p ?o}";
        TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, request);
        query.setIncludeInferred(false);
        TupleQueryResult result = query.evaluate();
        boolean retVal = false;
        BindingSet bindingSet = result.next();
        Value v = bindingSet.getValue("cnt");
        int cnt = Integer.valueOf(v.stringValue());
        if( cnt > 0) {
            retVal = true;
        }
        result.close();
        con.close();
        return retVal;
    }

}
