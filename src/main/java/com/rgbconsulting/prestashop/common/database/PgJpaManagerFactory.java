package com.rgbconsulting.prestashop.common.database;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author LuisCarlosGonzalez
 */
public class PgJpaManagerFactory implements Serializable {

    private static final long serialVersionUID = 2023060720230607L;
    private final static PgJpaManagerFactory instance = new PgJpaManagerFactory();
    private final String persistenceXml = "META-INF/persistence.xml";
    private final String puName = "prestashop";
    private EntityManagerFactory dbFactory = null;

    private PgJpaManagerFactory() {
        if (instance != null) {
            throw new IllegalStateException("El objeto solo se puede inicializar una vez");
        }
        initRepositoriesFactories();
    }

    public static PgJpaManagerFactory getInstance() {
        return instance;
    }

    public EntityManagerFactory getDbFactory() {
        if (dbFactory == null) {
            initializeDbFactory();
        }
        return dbFactory;
    }

    public void setDbFactory(EntityManagerFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    private EntityManagerFactory initializeDbFactory() {
        try {
            //List<PersistenceProvider> providerList = PersistenceProviderResolverHolder.getPersistenceProviderResolver().getPersistenceProviders();
            Map<String, String> dbProps = new HashMap<>();
            //dbProps.put(PersistenceUnitProperties., )
            dbProps.put("rgb.persistence.unit.name", puName);
            dbProps.put("jakarta.persistence.provider", "org.eclipse.persistence.jpa.PersistenceProvider");
            dbProps.put("eclipselink.persistencexml", persistenceXml);
            System.out.println("persistence.xml -> " + this.getClass().getClassLoader().getResource(persistenceXml));
            dbFactory = Persistence.createEntityManagerFactory(puName, dbProps);
        } catch (Exception e) {
            dbFactory = null;
        }
        return dbFactory;
    }

    public EntityManagerFactory getFactory() {
        return getDbFactory();
    }

    private void initRepositoriesFactories() {
        initializeDbFactory();
    }

    public static EntityManager getEntityManager() {
        try {
            //Class.forName("org.postgresql.Driver");
            //Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb", "luiscarlosgonzalez", "postgres");
            return getInstance().getDbFactory().createEntityManager();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Evita que por accesos externos indevidos se pierda el comportamiento Singleton del objeto
    private Object readResolve() throws ObjectStreamException {
        return instance;
    }

    private Object writeReplace() throws ObjectStreamException {
        return instance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("El objeto no puede ser clonado");
    }

    private static Class getClass(String classname) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = PgJpaManagerFactory.class.getClassLoader();
        }
        return (classLoader.loadClass(classname));
    }

    @PreDestroy
    public void releaseReferences() {
        if (instance.getDbFactory() != null && instance.getDbFactory().isOpen()) {
            try {
                instance.getDbFactory().close();
            } catch (Exception e) {
            }
        }
        instance.setDbFactory(null);
    }
}
