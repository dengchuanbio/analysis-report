package org.reactome.server.tools.analysis.exporter.util;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.element.Paragraph;
import org.reactome.server.tools.analysis.exporter.constant.Fonts;
import org.reactome.server.tools.analysis.exporter.element.P;
import org.reactome.server.tools.analysis.exporter.profile.Profile;

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

	public static Paragraph parseParagraph(String html) {
		List<Span> spans = new LinkedList<>();
		spans.add(new Text(html));
		spans = trimItalic(spans);
		spans = trimBold(spans);
		spans = trimSubs(spans);
		spans = trimLinks(spans);
		final P p = new P();
		spans.forEach(span -> span.render(p));
		return p;
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

	static abstract class Span {
		String text;

		Span(String text) {
			this.text = text;
		}

		abstract void render(Paragraph paragraph);
	}

	static class Text extends Span {
		Text(String text) {
			super(text);
		}

		@Override
		void render(Paragraph paragraph) {
			paragraph.add(text);
		}
	}

	static class Italic extends Span {

		Italic(String text) {
			super(text);
		}

		@Override
		void render(Paragraph paragraph) {
			paragraph.add(new com.itextpdf.layout.element.Text(text).setFont(Fonts.ITALIC));

		}
	}

	private static class Bold extends Span {
		Bold(String text) {
			super(text);
		}

		@Override
		void render(Paragraph paragraph) {
			paragraph.add(new com.itextpdf.layout.element.Text(text).setFont(Fonts.BOLD));
		}
	}

	private static class Link extends Span {
		private final String link;

		Link(String text, String link) {
			super(text);
			this.link = link;
		}

		@Override
		void render(Paragraph paragraph) {
			if (link == null)
				paragraph.add(text);
			else
				paragraph
						.add(new com.itextpdf.layout.element.Text(text)
								.setAction(PdfAction.createURI(link))
								.setFontColor(Profile.LINK_COLOR));
		}

	}

	private static class Sub extends Span {
		Sub(String text) {
			super(text);
		}

		@Override
		void render(Paragraph paragraph) {
			paragraph.add(new com.itextpdf.layout.element.Text(text).setFontSize(1 + Profile.P / 2));
		}
	}
}