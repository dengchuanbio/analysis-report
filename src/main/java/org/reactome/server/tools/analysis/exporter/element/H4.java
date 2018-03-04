package org.reactome.server.tools.analysis.exporter.element;

import com.itextpdf.layout.element.Text;
import org.reactome.server.tools.analysis.exporter.style.PdfProfile;

public class H4 extends P {
	public H4(String text) {
		this(new Text(text));
	}

	public H4(Text text) {
		super(text);
		style();
	}

	public H4() {
		style();
	}

	@Override
	protected void style() {
		setFont(PdfProfile.REGULAR);
		setFontSize(PdfProfile.H4);
	}
}
