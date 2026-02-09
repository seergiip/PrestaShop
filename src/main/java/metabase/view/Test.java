package metabase.view;

import metabase.controller.controllerMetaBase;

/**
 *
 * @author sergi
 */
public class Test {

    public static void main(String[] args) {
        controllerMetaBase mb = new controllerMetaBase();

        // Des de Odoo/Java autenticar-se a l'API de metabase (Obtenció de token d'autorització)
        //mb.getAuth();
        // Llistar informació des de metabase via API des de Odoo/Java
        //mb.listInfo();
        // Crear informació a metabase via API des de Odoo/Java
        // Ventas per mes/día
        String data
                = "{\n"
                + "  \"dataset_query\": {\n"
                + "    \"database\": 2,\n"
                + "    \"type\": \"native\",\n"
                + "    \"native\": {\n"
                + "      \"query\": \"SELECT DATE_TRUNC('week', (\\\"public\\\".\\\"sale_order\\\".\\\"date_order\\\" + INTERVAL '1 day')) + INTERVAL '-1 day' AS date_order, COUNT(*) AS count FROM \\\"public\\\".\\\"sale_order\\\" GROUP BY 1 ORDER BY 1 ASC\",\n"
                + "      \"template-tags\": {}\n"
                + "    }\n"
                + "  },\n"
                + "  \"name\": \"Ventas per mes/día\",\n"
                + "  \"collection_id\": 6,\n"
                + "  \"display\": \"line\",\n"
                + "  \"visualization_settings\": {}\n"
                + "}";

        //mb.createCardInfo(data);

        // Usuarios activos
        String data2
                = "{\n"
                + "  \"dataset_query\": {\n"
                + "    \"database\": 2,\n"
                + "    \"type\": \"native\",\n"
                + "    \"native\": {\n"
                + "      \"query\": \"SELECT\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"id\\\" AS \\\"id\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"company_id\\\" AS \\\"company_id\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"partner_id\\\" AS \\\"partner_id\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"active\\\" AS \\\"active\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"create_date\\\" AS \\\"create_date\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"login\\\" AS \\\"login\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"password\\\" AS \\\"password\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"action_id\\\" AS \\\"action_id\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"create_uid\\\" AS \\\"create_uid\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"write_uid\\\" AS \\\"write_uid\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"signature\\\" AS \\\"signature\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"share\\\" AS \\\"share\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"write_date\\\" AS \\\"write_date\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"totp_secret\\\" AS \\\"totp_secret\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"notification_type\\\" AS \\\"notification_type\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"odoobot_state\\\" AS \\\"odoobot_state\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"odoobot_failed\\\" AS \\\"odoobot_failed\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"website_id\\\" AS \\\"website_id\\\",\n"
                + "  \\\"public\\\".\\\"res_users\\\".\\\"sale_team_id\\\" AS \\\"sale_team_id\\\"\n"
                + "FROM \\\"public\\\".\\\"res_users\\\"\n"
                + "WHERE \\\"public\\\".\\\"res_users\\\".\\\"active\\\" = TRUE\n"
                + "LIMIT 1048575\",\n"
                + "      \"template-tags\": {}\n"
                + "    }\n"
                + "  },\n"
                + "  \"name\": \"Usuarios activos\",\n"
                + "  \"collection_id\": 6,\n"
                + "  \"display\": \"line\",\n"
                + "  \"visualization_settings\": {}\n"
                + "}";

        //mb.createCardInfo(data2);
        
        // Create user
        String first_name = "Pepitoo";
        String last_name = "DeLosPalotes";
        String email = "pepitoo@gmail.com";
        Integer id = 1;
        Boolean is_group_manager = false;
        //mb.createUser(first_name, last_name, email, id, is_group_manager);
        
        // Assigne user to Group
        Integer group_id = 4;
        Integer user_id = 8;
        
        //mb.addUserToGroup(group_id, is_group_manager, user_id);
        
        // Creation of group
        String name = "RRHH";
        Boolean is_tenant_group = false;
        
        //mb.createGroup(name, is_tenant_group);
        
        // Crear una coleccio
        // mb.postCollection(); // Salta error 403 
        
        // Crear un dashboard privat
        mb.createDashBoard(); // Salta error 403 
        
        // Fer un dashboard public
        String dashboard_id = "3";
        //String uuid = mb.makeDashBoardPublic(dashboard_id);
        //System.out.println("uuid: " + uuid);
        
        //mb.deleteDashBoardPublicLink(dashboard_id);
    }
}
