package org.reactome.server.tools.analysis.report;

import org.junit.*;
import org.reactome.server.analysis.core.result.AnalysisStoredResult;
import org.reactome.server.analysis.core.result.utils.TokenUtils;
import org.reactome.server.graph.service.DiagramService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.reactome.server.tools.analysis.report.exception.AnalysisExporterException;
import org.reactome.server.tools.analysis.report.util.GraphCoreConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Chuan-Deng dengchuanbio@gmail.com
 */
public class AnalysisReportTest {

	private static final boolean save = true;
	private static final HashMap<String, String> tokens = new HashMap<String, String>() {
		{
			put("overlay01", "MjAxODAyMTIxMTI5MzdfMQ==");
			put("overlay02", "MjAxODAyMTIxMTMwMTRfMg==");
			put("expression01", "MjAxODAyMTIxMTMwNDhfMw==");
			put("expression02", "MjAxODAyMTIxMTMxMTZfNA==");
			put("expression03", "MjAxODAzMDIwNTM2MDNfMQ%253D%253D");
			put("species", "MjAxODAyMTIxMTMyMzdfNQ==");
		}
	};
	private static final File SAVE_TO = new File("test-files");
	private static final String ANALYSIS_PATH = "src/test/resources/org/reactome/server/tools/analysis/report/analysis";
	private static final String DIAGRAM_PATH = "src/test/resources/org/reactome/server/tools/analysis/report/diagram";
	private static final String FIREWORKS_PATH = "src/test/resources/org/reactome/server/tools/analysis/report/fireworks";
	private static final String EHLD_PATH = "src/test/resources/org/reactome/server/tools/analysis/report/ehld";
	private static final String SVG_SUMMARY = "src/test/resources/org/reactome/server/tools/analysis/report/ehld/svgSummary.txt";
	private static AnalysisReport RENDERER;

	static {
	}

	@BeforeClass
	public static void beforeClass() {
		if (!SAVE_TO.exists() && !SAVE_TO.mkdirs())
			Assert.fail("Couldn't create test directory: " + SAVE_TO.getAbsolutePath());
		ReactomeGraphCore.initialise("localhost", "7474", "neo4j", "reactome", GraphCoreConfig.class);
		RENDERER = new AnalysisReport(DIAGRAM_PATH, EHLD_PATH, FIREWORKS_PATH, ANALYSIS_PATH, SVG_SUMMARY);
	}

	@AfterClass
	public static void afterClass() {
		if (!save) {
			final File[] files = SAVE_TO.listFiles();
			if (files != null)
				for (File file : files)
					if (!file.delete())
						Assert.fail("Couldn't delete test file: " + file.getAbsolutePath());
			if (!SAVE_TO.delete())
				Assert.fail("Couldn't delete test directory: " + SAVE_TO.getAbsolutePath());
		}
	}

	/**
	 * Handy method that will discover which diagrams you will have to add to
	 * resources
	 */
	@Ignore
	public void printPathways() {
		final DiagramService service = ReactomeGraphCore.getService(DiagramService.class);
		final Set<String> pathways = new TreeSet<>();
		final TokenUtils tokenUtils = new TokenUtils(ANALYSIS_PATH);
		tokens.forEach((name, token) -> {
			final AnalysisStoredResult result = tokenUtils.getFromToken(token);
			final AnalysisData data = new AnalysisData(result, "TOTAL", 48887L, 25);
			data.getPathways().forEach(pathwayData -> pathways.add(service.getDiagramResult(pathwayData.getSummary().getStId()).getDiagramStId()));
		});
		pathways.forEach(System.out::println);
	}

	@Test
	public void exportTest() {
		for (Map.Entry<String, String> entry : tokens.entrySet()) {
			final String type = entry.getKey();
			final String token = entry.getValue();
			try {
				final OutputStream os = new FileOutputStream(new File(SAVE_TO, String.format("%s.pdf", type)));
				RENDERER.create(token, "UNIPROT", 48887L, 25, "modern", "copper plus", "barium lithium", os);
			} catch (AnalysisExporterException | FileNotFoundException e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		}
	}

}
