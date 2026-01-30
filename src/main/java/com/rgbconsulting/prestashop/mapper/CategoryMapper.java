package com.rgbconsulting.prestashop.mapper;

/**
 *
 * @author sergi
 */
public class CategoryMapper {
    private String name;
    private String id_parent;
    private String active;
    private String link_rewrite;
    
    
    public CategoryMapper () {
        this.name = "";
        this.id_parent = "";
        this.active = "1";
        this.link_rewrite = "";
        
    }
    
    public CategoryMapper (String name, String id_parent, String active) {
        this.name = name;
        this.id_parent = id_parent;
        this.active = active;
        this.link_rewrite = name.toLowerCase()
                        .trim()
                        .replace(" ", "-")
                        .replaceAll("[^a-z0-9\\-]", "");
    }

    public String xmlCreationCategory() {
        return "<prestashop xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n"
                + "  <category>\n"
                + "    <id></id>\n"
                + "    <id_parent>" + this.id_parent + "</id_parent>\n"
                + "    <active>" + this.active + "</active>\n"
                + "    <id_shop_default><![CDATA[]]></id_shop_default>\n"
                + "    <is_root_category><![CDATA[]]></is_root_category>\n"
                + "    <position><![CDATA[]]></position>\n"
                + "    <date_add><![CDATA[]]></date_add>\n"
                + "    <date_upd><![CDATA[]]></date_upd>\n"
                + "    <name>\n"
                + "      <language id=\"1\">"+ this.name +"</language>\n"
                + "      <language id=\"2\">"+ this.name +"</language>\n"
                + "    </name>\n"
                + "    <link_rewrite>\n"
                + "      <language id=\"1\">"+ this.link_rewrite +"</language>\n"
                + "      <language id=\"2\">"+ this.link_rewrite +"</language>\n"
                + "    </link_rewrite>\n"
                + "    <description>\n"
                + "      <language id=\"1\"><![CDATA[]]></language>\n"
                + "      <language id=\"2\"><![CDATA[]]></language>\n"
                + "    </description>\n"
                + "    <meta_title>\n"
                + "      <language id=\"1\"><![CDATA[]]></language>\n"
                + "      <language id=\"2\"><![CDATA[]]></language>\n"
                + "    </meta_title>\n"
                + "    <meta_description>\n"
                + "      <language id=\"1\"><![CDATA[]]></language>\n"
                + "      <language id=\"2\"><![CDATA[]]></language>\n"
                + "    </meta_description>\n"
                + "    <meta_keywords>\n"
                + "      <language id=\"1\"><![CDATA[]]></language>\n"
                + "      <language id=\"2\"><![CDATA[]]></language>\n"
                + "    </meta_keywords>\n"
                + "    <associations>\n"
                + "      <categories>\n"
                + "        <category>\n"
                + "          <id><![CDATA[]]></id>\n"
                + "        </category>\n"
                + "      </categories>\n"
                + "      <products>\n"
                + "        <product>\n"
                + "          <id><![CDATA[]]></id>\n"
                + "        </product>\n"
                + "      </products>\n"
                + "    </associations>\n"
                + "  </category>\n"
                + "</prestashop>";
    }
    
    
    public String getLinkRewrite () {
        return this.link_rewrite;
    }

    public String getName() {
        return this.name;
    }

    public String getId_parent() {
        return this.id_parent;
    }

    public String getId() {
        return null; //acabar de fer
    }
    public String getActive() {
        return this.active;
    }
}
