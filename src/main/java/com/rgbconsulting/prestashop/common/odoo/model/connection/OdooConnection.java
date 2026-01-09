package com.rgbconsulting.prestashop.common.odoo.model.connection;

import com.rgb.training.app.common.odoo.types.Recordset;
import com.rgb.training.app.common.odoo.types.Values;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * A simple Odoo Connection Helper.
 */
public class OdooConnection {

    private static final Object[] EMPTY_PARAMS = new Object[0];

    private String url;
    private String db;
    private String uid;
    private String pwd;

    private XmlRpcClient commonClient;
    private XmlRpcClient objectClient;

    public OdooConnection() {
        
    }

    public OdooConnection(String url, String db, String uid, String pwd) throws MalformedURLException {

        //odoo
        this.url = url;
        this.db = db;
        this.uid = uid;
        this.pwd = pwd;
        this.commonClient = buildClient("%s/xmlrpc/2/common");
        this.objectClient = buildClient("%s/xmlrpc/2/object");
    }

    public Boolean initializeConnection(String url, String db, String uid, String pwd) {
        try {
            this.url = url;
            this.db = db;
            this.uid = uid;
            this.pwd = pwd;
            this.commonClient = buildClient("%s/xmlrpc/2/common");
            this.objectClient = buildClient("%s/xmlrpc/2/object");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // Common
    public String version() throws XmlRpcException {
        return commonClient.execute("version", EMPTY_PARAMS).toString();
    }

    // ORM
    public Integer search_count(String model) throws XmlRpcException {
        return search_count(model, Values.empty());
    }

    public Integer search_count(String model, Values domain) throws XmlRpcException {
        Values args = Values.create(domain);
        return (Integer) execute_kw(model, "search_count", args);
    }

    public Values search(String model) throws XmlRpcException {
        return search(model, Values.empty());
    }

    public Values search(String model, Values domain) throws XmlRpcException {
        Values args = Values.create(domain);
        return Values.from(execute_kw(model, "search", args));
    }

    public Recordset read(String model, Integer id) throws XmlRpcException {
        return read(model, Values.create(id), Values.empty());
    }

    public Recordset read(String model, Values ids) throws XmlRpcException {
        return read(model, ids, Values.empty());
    }

    public Recordset read(String model, Values ids, Values fields) throws XmlRpcException {
        Values args = Values.create(checkIsEmpty(ids), fields);
        Values rows = Values.from(execute_kw(model, "read", args));
        return Recordset.create(rows);
    }

    public Recordset search_read(String model) throws XmlRpcException {
        return search_read(model, Values.empty(), Values.empty());
    }

    public Recordset search_read(String model, Values domain) throws XmlRpcException {
        return search_read(model, domain, Values.empty());
    }

    public Recordset search_read(String model, Values domain, Values fields) throws XmlRpcException {
        Values args = Values.create(domain, fields);
        Values rows = Values.from(execute_kw(model, "search_read", args));
        return Recordset.create(rows);
    }

    //Parlar amb Luis i Eduard
    public Recordset search_read(String model, Values domain, Values fields, Integer offset, Integer maxresult) throws XmlRpcException {
        Values args = Values.create(domain, fields, offset, maxresult);
        Values rows = Values.from(execute_kw(model, "search_read", args));
        return Recordset.create(rows);
    }

    public Integer create(String model, Values fields) throws XmlRpcException {
        Values rows = Values.from(execute_kw(model, "create", fields));
        return Integer.valueOf(String.valueOf(rows.get(0)));
    }

    public Boolean update(String model, Values fields) throws XmlRpcException {
        Values rows = Values.from(execute_kw(model, "write", fields));
        return Boolean.valueOf(String.valueOf(rows.get(0)));
    }

    /**
     * Elimina uno o más registros del modelo especificado
     */
    public Recordset delete(String model, Values ids) throws XmlRpcException {
        Values args = Values.create(ids);
        Values rows = Values.from(execute_kw(model, "unlink", args));
        Recordset results = new Recordset();
        for (int i = 0; i < ids.size(); i++) {
            results.add(com.rgb.training.app.common.odoo.types.Record.fromKeyValue(ids.get(i), rows.get(i)));
        }
        return results;
    }

    public Object execute_kw(String model, String method, Values args) throws XmlRpcException {
        Values params = Values.create(db, Integer.parseInt(uid), pwd, model, method, args);
        return objectClient.execute("execute_kw", params);
    }

    // Internal
    private XmlRpcClient buildClient(String endpoint) throws MalformedURLException {
        URL rpcUrl = new URL(String.format(endpoint, url));
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(rpcUrl);
        //config.setEnabledForExtensions(Boolean.TRUE); //Por defecto =False ("server is strictly compliant to the XML-RPC spec")
        //
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        return client;
    }

    private Values checkIsEmpty(Values ids) {
        if (ids.isEmpty()) {
            ids.add("");
        }
        return ids;
    }

}
