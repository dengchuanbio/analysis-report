package org.reactome.server.tools.analysis.exporter.playground.util;

import org.reactome.server.graph.domain.model.Pathway;
import org.reactome.server.graph.service.DatabaseObjectService;
import org.reactome.server.graph.service.GeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chuan-Deng dengchuanbio@gmail.com
 */
public class GraphCoreHelper {

    private static final Logger logger = LoggerFactory.getLogger(GraphCoreHelper.class);
    private static ApplicationContext context;
    private static GeneralService genericService;
    private static DatabaseObjectService databaseObjectService;

    static {
        // TODO: 30/01/18 write neo4j config detail into xml file
        context = new AnnotationConfigApplicationContext(GraphCoreConfig.class);
        genericService = context.getBean(GeneralService.class);
        databaseObjectService = context.getBean(DatabaseObjectService.class);
    }

    /**
     * get pathways detail information from neo4j database by given the target pathway identifiers array.
     *
     * @param pathways {@see org.reactome.server.tools.analysis.exporter.playground.model.Pathway}
     * @return
     */
//    public static List<Pathway> getPathway(List<org.reactome.server.tools.analysis.exporter.playground.model.Pathway> pathways) {
    public static Pathway[] getPathway(List<org.reactome.server.tools.analysis.exporter.playground.model.Pathway> pathways) {
        long start = Instant.now().toEpochMilli();
        List<Object> identifiers = new ArrayList<>();
        pathways.forEach(pathway -> identifiers.add(pathway.getStId()));
        Pathway[] pathway = databaseObjectService.findByIdsNoRelations(identifiers).toArray(new Pathway[identifiers.size()]);
        logger.info("retrieve finish in :" + (Instant.now().toEpochMilli() - start) + " ms");
        return pathway;
    }

    /**
     * @return Reactome's current database version.
     */
    public static int getDBVersion() {
        return genericService.getDBVersion();
    }
}