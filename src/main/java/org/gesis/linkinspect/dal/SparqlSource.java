package org.gesis.linkinspect.dal;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.gesis.linkinspect.model.Predicate;
import org.gesis.linkinspect.model.RDFObject;
import org.gesis.linkinspect.model.ResourceProperty;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;
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
     * Request a given resource from the SPARQL endpoint and populate the list
     * with it.
     *
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
            RDFObject rdfObj = new RDFObject(object, endpoint.toString());
            ResourceProperty rp = new ResourceProperty(new Predicate(predicate, true), rdfObj);
            observableList.add(rp);
            asyncRefine(rp);
        }
        addInverse(resource);
        result.close();
        con.close();
    }

    /**
     * Adds resource that reference the given resource to the observable list.
     *
     * @param resource
     * @return
     */
    @SuppressWarnings("unchecked")
    public void addInverse(String resource) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
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
            RDFObject rdfObj = new RDFObject(subject, endpoint.toString());
            ResourceProperty rp = new ResourceProperty(new Predicate(predicate, false), rdfObj);
            observableList.add(rp);
            asyncRefine(rp);
        }
        result.close();
        con.close();
    }

    /**
     * Connects to a SPARQL endpoint and closes the connection immediately.
     *
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

    /**
     * Determines whether a given resource is present in the given sparql store
     *
     * @param sparqlEp The sparql store to search in
     * @param resource The resource to search
     * @return true, if the resource was found
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public static boolean isPresent(String sparqlEp, String resource) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
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
        if (cnt > 0) {
            retVal = true;
        }
        result.close();
        con.close();
        return retVal;
    }

    /**
     * Queries a resource description of the given resource and writes it into
     * an RDFObject.
     *
     * @param resource
     * @param obj
     */
    private void asyncRefine(ResourceProperty prop) {
        if (prop.getRefValue().isURI()) {
//            Thread t = new Thread(new MyRunnable(prop));
//            t.start();
            Platform.runLater(new MyRunnable(prop));
        }

    }

    private class MyRunnable implements Runnable {

        private ResourceProperty prop = null;

        public MyRunnable(ResourceProperty prop) {
            this.prop = prop;
        }

        @Override
        public void run() {
            try {
                Repository repo = new SPARQLRepository(endpoint.toString());
                repo.initialize();
                RepositoryConnection con = repo.getConnection();
                String request = "SELECT ?p ?o WHERE { <" + prop.getRefValue().getValue() + "> ?p ?o }";
                TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, request);
                query.setIncludeInferred(false);
                TupleQueryResult result = query.evaluate();
                String preview = "";
                while (result.hasNext()) {
                    BindingSet bindingSet = result.next();
                    Value predicate = bindingSet.getValue("p");
                    Value object = bindingSet.getValue("o");
                    if (object instanceof Literal) {
                        if (predicate.stringValue().endsWith("title")
                                || predicate.stringValue().endsWith("name")
                                || predicate.stringValue().endsWith("label")) {
                            preview = object.stringValue() + ";" + preview;
                        } else {
                            preview += object.stringValue() + ";";
                        }
                    }
                }
                result.close();
                con.close();
                prop.getRefValue().setPreview(preview);
                //ugly hack to refresh list
                observableList.add(null);
                observableList.remove(null);
            } catch (RepositoryException ex) {
                Logger.getLogger(SparqlSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedQueryException ex) {
                Logger.getLogger(SparqlSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (QueryEvaluationException ex) {
                Logger.getLogger(SparqlSource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
