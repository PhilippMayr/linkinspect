/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gesis.linkinspect.dal;

import java.net.URL;
import javafx.collections.ObservableList;
import org.gesis.linkinspect.model.ResourceDescription;
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
 *
 * @author bensmafx
 */
public class SparqlSource {

    private URL endpoint = null;
    private ObservableList observableList = null;

    public SparqlSource(URL endpoint, ObservableList list) {
        this.endpoint = endpoint;
        this.observableList = list;
    }

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
            ResourceProperty rp = new ResourceProperty(new URIImpl(predicate.stringValue()), object, true);
            observableList.add(rp);
        }
        result.close();
        con.close();
    }

    public ResourceDescription requestInverse(String resource) {
        return null;
    }

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

}
