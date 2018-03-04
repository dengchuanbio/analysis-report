package org.reactome.server.tools.analysis.exporter.util;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.property.HorizontalAlignment;
import org.reactome.server.analysis.core.result.AnalysisStoredResult;
import org.reactome.server.graph.domain.result.DiagramResult;
import org.reactome.server.tools.analysis.exporter.exception.AnalysisExporterException;
import org.reactome.server.tools.diagram.exporter.common.analysis.AnalysisException;
import org.reactome.server.tools.diagram.exporter.common.profiles.factory.DiagramJsonDeserializationException;
import org.reactome.server.tools.diagram.exporter.common.profiles.factory.DiagramJsonNotFoundException;
import org.reactome.server.tools.diagram.exporter.raster.RasterExporter;
import org.reactome.server.tools.diagram.exporter.raster.api.RasterArgs;
import org.reactome.server.tools.diagram.exporter.raster.ehld.exception.EhldException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Help to create the diagram image by invoking the Reactome {@link
 * RasterExporter}.
 *
 * @author Chuan Deng dengchuanbio@gmail.com
 */
public class DiagramHelper {
	private static final double IMAGE_SCALE = 1.5;  // 1=keep original ppp
	private static final int MIN_QUALITY = 3;
	private static RasterExporter exporter;

	/**
	 * create diagram image by using the RasterExporter{@see RasterExporter}.
	 *
	 * @param stId stable identifier of the diagram.
	 * @param asr  {@see AnalysisStoredResult} contains the diagram analysis
	 *             overlay information.
	 *
	 * @return diagram.
	 */
	public static Image getDiagram(String stId, AnalysisStoredResult asr, String resource, double pageWidth) throws AnalysisExporterException {
		final DiagramResult diagramResult = GraphCoreHelper.getDiagramResult(stId);
		final RasterArgs args = new RasterArgs(diagramResult.getDiagramStId(), "png");
		args.setSelected(diagramResult.getEvents());
		args.setWriteTitle(false);
		args.setResource(resource);

		final Integer width = diagramResult.getWidth();
		final double desiredWidth = Math.min(width, pageWidth) * IMAGE_SCALE;
		final double scale = desiredWidth / width;
		int quality = Math.max(toQuality(scale), MIN_QUALITY);
		args.setQuality(quality);

		try {
			final BufferedImage image = exporter.export(args, asr);
			final Image fImage = new Image(ImageDataFactory.create(image, Color.WHITE));
			fImage.setHorizontalAlignment(HorizontalAlignment.CENTER);


			final float factor = (float) Math.min(1. / IMAGE_SCALE, pageWidth / image.getWidth()) - 0.01f;
			fImage.scale(factor, factor);
			return fImage;
		} catch (DiagramJsonNotFoundException | AnalysisException | DiagramJsonDeserializationException | EhldException | IOException e) {
//			System.out.println("!!" + diagramResult.getDiagramStId());
			throw new AnalysisExporterException("Exception reading diagram", e);
		}
//		return null;
	}

	private static int toQuality(double scale) {
		if (scale <= 0.1) return 1;
		if (scale >= 3) return 10;
		double quality;
		if (scale <= 1) {
			quality = interpolate(scale, 0.1, 1, 1, 5);
		} else quality = interpolate(scale, 1, 3, 5, 10);
		return Math.toIntExact(Math.round(quality));
	}

	private static double interpolate(double value, double min, double max, double targetMin, double targetMax) {
		return (value - min) / (max - min) * (targetMax - targetMin) + targetMin;
	}

	public static void setPaths(String diagramPath, String ehldPath, String analysisPath, String svgSummary) {
		exporter = new RasterExporter(diagramPath, ehldPath, analysisPath, svgSummary);
	}
}
