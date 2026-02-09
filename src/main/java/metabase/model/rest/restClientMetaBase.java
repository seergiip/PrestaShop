package metabase.model.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 *
 * @author sergi
 */
public class restClientMetaBase {

    private static String URL = "http://localhost:3000/api/";
    private static String API_KEY = "mb_Jrwc2cVoT1JkWn8O+0qz1k5v/m8l/WDjgpZOchjc3xA=";
    private static String USERNAME = "sergipanec@gmail.com";
    private static String PASSWORD = "odoo1234";

    public HttpClient initClient() {

        try {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar HttpClient", e);
        }
    }

    /*
        Es un client per probar connection
     */
    public void tryAuth() {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "permissions/group"))
                    .header("x-api-key", this.API_KEY)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("GET Status Code: " + response.statusCode());
                System.out.println("GET Response Body: " + response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    // Llistar informació des de metabase via API des de Odoo/Java
    //  - Dashboards i cards
    public void listDashboards() {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "dashboard/1"))
                    .header("x-api-key", this.API_KEY)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("GET Status Code: " + response.statusCode());
                System.out.println("DASHBOARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    public void listCards() {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "card/?f=all"))
                    .header("x-api-key", this.API_KEY)
                    .GET()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("GET Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    // Crear informació a metabase via API des de Odoo/Java
    /* Crear card amb una consulta SQL simple
        Ejemplos:
        - Ventas per mes/día
        - Usuarios activos
     */
    public void createCardInfo(String data) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "card/"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(data))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    public void createUser(String first_name, String last_name, String email, Integer id, Boolean is_group_manager) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "user/"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(userData(first_name, last_name, email, id, is_group_manager)))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

    }

    private String userData(String first_name, String last_name, String email, Integer id, Boolean is_group_manager) {
        return "{\n"
                + "  \"email\": \"" + email + "\",\n"
                + "  \"first_name\": \"" + first_name + "\",\n"
                + "  \"last_name\": \"" + last_name + "\",\n"
                + "  \"login_attributes\": {\n"
                + "    \"propertyName*\": \"anything\"\n"
                + "  },\n"
                + "  \"source\": \"admin\",\n"
                + "  \"user_group_memberships\": [\n"
                + "    {\n"
                + "      \"id\": " + id + ",\n"
                + "      \"is_group_manager\": " + is_group_manager + "\n"
                + "    }\n"
                + "  ]\n"
                + "}";
    }

    public void createGroup(String name, Boolean is_tenant_group) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "permissions/group"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(groupData(name, is_tenant_group)))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

    }

    private String groupData(String name, Boolean is_tenant_group) {
        return "{\n"
                + "  \"is_tenant_group\": " + is_tenant_group.toString() + ",\n"
                + "  \"name\": \"" + name + "\"\n"
                + "}";

    }

    public void addUserToGroup(Integer group_id, Boolean is_group_manager, Integer user_id) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "permissions/membership"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(userToGroupInfo(group_id, is_group_manager, user_id)))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("CARD INFO\n" + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    private String userToGroupInfo(Integer group_id, Boolean is_group_manager, Integer user_id) {
        return "{\n"
                + "  \"group_id\": " + group_id.toString() + ",\n"
                + "  \"is_group_manager\": " + is_group_manager.toString() + ",\n"
                + "  \"user_id\": " + user_id.toString() + "\n"
                + "}";
    }

    private String getSessionId(String json) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response = null;
        String id;
        String regex[];
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "session/"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

        regex = response.body().toString().split(":");
        id = regex[1];
        id = id.substring(1, id.length() - 2);
        System.out.println("Id: " + id);

        return id;
    }

    public void postCollection() {
        String sessionJson = "{"
                + "\"username\": \"" + this.USERNAME + "\","
                + "\"password\": \"" + this.PASSWORD + "\""
                + "}";
        String collectionJson = "{"
                + "\"authority_level\": \"official\","
                + "\"description\": \"This is a test\","
                + "\"name\": \"test\","
                + "\"namespace\": \"test\","
                + "\"parent_id\": 1"
                + "}";

        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "collection"))
                    .header("x-api-key", this.API_KEY)
                    .header("X-Metabase-Session", getSessionId(sessionJson))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(collectionJson))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("POST Body: " + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    public void createDashBoard() {
        String sessionJson = "{"
                + "\"username\": \"" + this.USERNAME + "\","
                + "\"password\": \"" + this.PASSWORD + "\""
                + "}";
        String dashboardJson = "{"
                + "  \"name\": \"Dashboard de Proba\","
                + "  \"description\": \"Aixo es un dashboard de proba\","
                + "  \"collection_id\": 1"
                + "}";
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "dashboard"))
                    .header("x-api-key", this.API_KEY)
                    .header("X-Metabase-Session", getSessionId(sessionJson))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(dashboardJson))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("POST Body: " + response.body());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }
    }

    public String makeDashBoardPublic(String dashboard_id) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response = null;
        String regex[];
        String uuid;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "dashboard/" + dashboard_id + "/public_link"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString("{}"))
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("POST Status Code: " + response.statusCode());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

        regex = response.body().toString().split(":");
        uuid = regex[1];
        uuid = uuid.substring(1, uuid.length() - 2);
        return uuid;
    }

    public void deleteDashBoardPublicLink(String dashboard_id) {
        HttpClient client = initClient();
        HttpRequest request = null;
        HttpResponse response = null;

        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(this.URL + "dashboard/" + dashboard_id + "/public_link"))
                    .header("x-api-key", this.API_KEY)
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
        } catch (URISyntaxException u) {
            u.printStackTrace();
        }

        if (request != null) {
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("-------------------------------------");
                System.out.println("DELETE Status Code: " + response.statusCode());
                System.out.println("-------------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error: La solicitud no ha pogut ser creada degut a un problema amb la URI.");
        }

    }
}
