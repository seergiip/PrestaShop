
package metabase.controller;

import metabase.model.rest.restClientMetaBase;

/**
 *
 * @author sergi
 */
public class controllerMetaBase {
    restClientMetaBase clientMetaBase = new restClientMetaBase();
    
    public void getAuth() {
        clientMetaBase.tryAuth();
    }
    
    public void listInfo() {
        clientMetaBase.listDashboards();
        
        clientMetaBase.listCards();
    }
    
    public void createCardInfo(String data) {
        clientMetaBase.createCardInfo(data);
    }
    
    public void createUser(String first_name, String last_name, String email, Integer id, Boolean is_group_manager)  {
        clientMetaBase.createUser(first_name, last_name, email, id, is_group_manager);
    }
    
    public void createGroup(String name, Boolean is_tenant_group) {
        clientMetaBase.createGroup(name, is_tenant_group);
    }
    
    public void addUserToGroup(Integer group_id, Boolean is_group_manager, Integer user_id) {
        clientMetaBase.addUserToGroup(group_id, is_group_manager, user_id);
    }
    public void postCollection()  {
        clientMetaBase.postCollection();
    }
    
    public void createDashBoard() {
        clientMetaBase.createDashBoard();
    }
    
    public String makeDashBoardPublic(String dashboard_id) {
        return clientMetaBase.makeDashBoardPublic(dashboard_id);
    }
    
    public void deleteDashBoardPublicLink (String dashboard_id) {
        clientMetaBase.deleteDashBoardPublicLink(dashboard_id);
    }
}
