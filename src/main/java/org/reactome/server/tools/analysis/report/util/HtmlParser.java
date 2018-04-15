package org.reactome.server.tools.analysis.report.util;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.reactome.server.graph.domain.model.Summation;
import org.reactome.server.tools.analysis.report.document.TexDocument;
import org.reactome.server.tools.analysis.report.document.TextUtils;
import org.reactome.server.tools.analysis.report.style.PdfProfile;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

	private static final Pattern SUB = Pattern.compile("(?i)<sub>(.*?)</sub>");
	private static final Pattern HREF = Pattern.compile("(?i)href=\"(.*?)\"");
	private static final Pattern A = Pattern.compile("(?i)<a(.*?)>(.*?)</a>");
	private static final Pattern ITALIC = Pattern.compile("(?i)<i>(.*?)</i>");
	private static final Pattern BOLD = Pattern.compile("(?i)<b>(.*?)</b>");

	public static Paragraph parseParagraph(String html, PdfProfile profile) {
		List<Span> spans = new LinkedList<>();
		spans.add(new Text(html));
		spans = trimItalic(spans);
		spans = trimBold(spans);
		spans = trimSubs(spans);
		spans = trimLinks(spans);
		final Paragraph paragraph = profile.getParagraph("");
		spans.forEach(span -> span.render(paragraph, profile));
		return paragraph;
	}

	public static void parseText(Document document, PdfProfile profile, Summation summation) {
		final String[] paragraphs = summation.getText().split("(?i)<br>|<p>");
		for (String paragraph : paragraphs) {
			final String trim = paragraph.trim();
			if (trim.isEmpty()) continue;
			document.add(parseParagraph(trim, profile));
		}
	}

	private static List<Span> trimItalic(List<Span> in) {
		List<Span> spans = new LinkedList<>();
		for (Span span : in) {
			if (span instanceof Text) {
				final String text = span.text;
				final Matcher matcher = ITALIC.matcher(text);
				int start = 0;
				while (matcher.find()) {
					spans.add(new Text(text.substring(start, matcher.start())));
					spans.add(new Italic(matcher.group(1)));
					start = matcher.end();
				}
				spans.add(new Text(text.substring(start)));
			} else spans.add(span);
		}
		return spans;
	}

	private static List<Span> trimBold(List<Span> in) {
		List<Span> spans = new LinkedList<>();
		for (Span span : in) {
			if (span instanceof Text) {
				final String text = span.text;
				final Matcher matcher = BOLD.matcher(text);
				int start = 0;
				while (matcher.find()) {
					spans.add(new Text(text.substring(start, matcher.start())));
					spans.add(new Bold(matcher.group(1)));
					start = matcher.end();
				}
				spans.add(new Text(text.substring(start)));
			} else spans.add(span);
		}
		return spans;
	}

	private static List<Span> trimSubs(List<Span> in) {
		List<Span> spans = new LinkedList<>();
		for (Span span : in) {
			if (span instanceof Text) {
				final String text = span.text;
				final Matcher matcher = SUB.matcher(text);
				int start = 0;
				while (matcher.find()) {
					spans.add(new Text(text.substring(start, matcher.start())));
					spans.add(new Sub(matcher.group(1)));
					start = matcher.end();
				}
				spans.add(new Text(text.substring(start)));
			} else spans.add(span);
		}
		return spans;
	}

	private static List<Span> trimLinks(List<Span> in) {
		List<Span> spans = new LinkedList<>();
		for (Span span : in) {
			if (span instanceof Text) {
				final String text = span.text;
				final Matcher matcher = A.matcher(text);
				int start = 0;
				while (matcher.find()) {
					spans.add(new Text(text.substring(start, matcher.start())));
					String linkText = matcher.group(2);
					String link = extractLink(matcher.group(1));
					spans.add(new Link(linkText, link));
					start = matcher.end();
				}
				spans.add(new Text(text.substring(start)));
			} else spans.add(span);
		}
		return spans;
	}

	private static String extractLink(String text) {
		final Matcher matcher = HREF.matcher(text);
		if (matcher.find())
			return matcher.group(1);
		return null;
	}

	public static void parseText(TexDocument document, Summation summation) {
		final String[] paragraphs = summation.getText().split("(?i)<br>|<p>");
		for (String paragraph : paragraphs) {
			final String trim = paragraph.trim();
			if (trim.isEmpty()) continue;
			parseParagraph(document, trim);
			document.ln().ln();
		}
	}

	public static void parseParagraph(TexDocument document, String html) {
		List<Span> spans = new LinkedList<>();
		spans.add(new Text(html));
		spans = trimItalic(spans);
		spans = trimBold(spans);
		spans = trimSubs(spans);
		spans = trimLinks(spans);
		spans.forEach(span -> span.render(document));
	}

	static abstract class Span {
		String text;

		Span(String text) {
			this.text = text;
		}

		abstract void render(Paragraph paragraph, PdfProfile profile);

		public abstract void render(TexDocument document);
	}

	static class Text extends Span {
		Text(String text) {
			super(text);
		}

		@Override
		void render(Paragraph paragraph, PdfProfile profile) {
			paragraph.add(text);
		}

		@Override
		public void render(TexDocument document) {
			document.text(TextUtils.scape(text));
		}
	}

	static class Italic extends Span {

		Italic(String text) {
			super(text);
		}

		@Override
		void render(Paragraph paragraph, PdfProfile profile) {
			paragraph.add(new com.itextpdf.layout.element.Text(text).setFont(profile.getItalic()));

		}

		@Override
		public void render(TexDocument document) {
			document.command(TexDocument.TEXT_IT, TextUtils.scape(text));
		}
	}

	private static class Bold extends Span {
		Bold(String text) {
			super(text);
		}

		@Override
		void render(Paragraph paragraph, PdfProfile profile) {
			paragraph.add(new com.itextpdf.layout.element.Text(text).setFont(profile.getBold()));
		}

		@Override
		public void render(TexDocument document) {
			document.command(TexDocument.TEXT_BF, TextUtils.scape(text));
		}
	}

	private static class Link extends Span {
		private final String link;

		Link(String text, String link) {
			super(text);
			this.link = link;
		}

		@Override
		void render(Paragraph paragraph, PdfProfile profile) {
			if (link == null)
				paragraph.add(text);
			else
				paragraph.add(new com.itextpdf.layout.element.Text(text)
						.setAction(PdfAction.createURI(link))
						.setFontColor(profile.getLinkColor()));
		}

		@Override
		public void render(TexDocument document) {
			document.command(TexDocument.HYPERREF, TextUtils.scape(text), link);
		}

	}

	private static class Sub extends Span {

		Sub(String text) {
			super(text);
		}

		@Override
		void render(Paragraph paragraph, PdfProfile profile) {
			paragraph.add(new com.itextpdf.layout.element.Text(text).setFontSize(1 + profile.getFontSize() / 2));
		}

		@Override
		public void render(TexDocument document) {
			document.command(TexDocument.TEXT_SUBSCRIPT, TextUtils.scape(text));
		}
	}
}
