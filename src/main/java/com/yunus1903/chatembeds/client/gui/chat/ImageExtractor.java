package com.yunus1903.chatembeds.client.gui.chat;

import java.io.IOException;
import java.net.URL;

import javax.annotation.Nullable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * This class contains multiple methods to extract images from URL's
 *
 * @see <a href=
 *      "https://gist.github.com/Daenyth/4742267">https://gist.github.com/Daenyth/4742267</a>
 * @author Yunus1903
 * @since 03/09/2020
 */
public class ImageExtractor {
	@Nullable
	public static String extractImageURL(final URL url) throws IOException {
		return extractImageURL(url.toString());
	}

	@Nullable
	public static String extractImageURL(final String url) throws IOException {
		final Connection connection = Jsoup.connect(url).ignoreContentType(true);
		connection.request().method(Connection.Method.GET);
		final Connection.Response response = connection.execute();
		final String contentType = response.contentType();
		if (contentType != null && contentType.startsWith("image/")) {
			return url;
		}

		final Document document = response.parse();
		String imageUrl;

		imageUrl = getImageFromSchema(document);
		if (imageUrl != null) {
			return imageUrl;
		}

		imageUrl = getImageFromOpenGraph(document);
		if (imageUrl != null) {
			return imageUrl;
		}

		imageUrl = getImageFromTwitterCard(document);
		if (imageUrl != null) {
			return imageUrl;
		}

		imageUrl = getImageFromTwitterShared(document);
		if (imageUrl != null) {
			return imageUrl;
		}

		return getImageFromLinkRel(document);
	}

	@Nullable
	private static String getImageFromSchema(final Document document) {
		final Element container = document.select("*[itemscope][itemtype=http://schema.org/ImageObject]").first();
		if (container == null) {
			return null;
		}

		final Element image = container.select("img[itemprop=contentUrl]").first();
		if (image == null) {
			return null;
		}
		return image.absUrl("src");
	}

	@Nullable
	private static String getImageFromOpenGraph(final Document document) {
		final Element image = document.select("meta[property=og:image]").first();
		if (image != null) {
			return image.attr("abs:content");
		}
		final Element secureImage = document.select("meta[property=og:image:secure]").first();
		if (secureImage != null) {
			return secureImage.attr("abs:content");
		}
		return null;
	}

	@Nullable
	private static String getImageFromTwitterCard(final Document document) {
		final Element meta = document.select("meta[name=twitter:card][content=photo]").first();
		if (meta == null) {
			return null;
		}
		final Element image = document.select("meta[name=twitter:image]").first();
		return image.attr("abs:content");
	}

	@Nullable
	private static String getImageFromTwitterShared(final Document document) {
		final Element div = document.select("div.media-gallery-image-wrapper").first();
		if (div == null) {
			return null;
		}
		final Element img = div.select("img.media-slideshow-image").first();
		if (img != null) {
			return img.absUrl("src");
		}
		return null;
	}

	@Nullable
	private static String getImageFromLinkRel(final Document document) {
		final Element link = document.select("link[rel=image_src]").first();
		if (link != null) {
			return link.attr("abs:href");
		}
		return null;
	}
}