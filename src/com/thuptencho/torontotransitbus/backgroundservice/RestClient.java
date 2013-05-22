package com.thuptencho.torontotransitbus.backgroundservice;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.models.Pathz;
import com.thuptencho.torontotransitbus.models.Pointz;
import com.thuptencho.torontotransitbus.models.Route;
import com.thuptencho.torontotransitbus.models.Stop;

public class RestClient {
	private static final String REST_URL_BASE = "http://webservices.nextbus.com/service/publicXMLFeed?a=ttc";

	private static String getContentFromUrl(String urlString)
			throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {

			public void process(final HttpRequest request,
					final HttpContext context) throws HttpException,
					IOException {
				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
			}

		});

		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {

			public void process(final HttpResponse response,
					final HttpContext context) throws HttpException,
					IOException {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					Header ceheader = entity.getContentEncoding();
					if (ceheader != null) {
						HeaderElement[] codecs = ceheader.getElements();
						for (int i = 0; i < codecs.length; i++) {
							if (codecs[i].getName().equalsIgnoreCase("gzip")) {
								response.setEntity(new GzipDecompressingEntity(
										response.getEntity()));
								return;
							}
						}
					}
				}
			}

		});
		try {
			HttpGet httpget = new HttpGet(urlString);
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpget, responseHandler);
			return responseBody;

		} finally {
			httpClient.getConnectionManager().shutdown();
		}

	}

	/*
	 * ONLY a service should call this method ..should call insert/update on
	 * provider in the end of this method
	 */
	public static List<Route> getRoutes() throws XmlPullParserException,
			IOException {

		String urlString = RestClient.getRestUrlForRoutelist();
		String str = RestClient.getContentFromUrl(urlString);

		List<Route> routes = new ArrayList<Route>();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(str));

		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				String tagname = xpp.getName().toString();
				String routeString = "route";
				if (tagname.equalsIgnoreCase(routeString)) {
					// found all route tags, get their attributes and values.
					String tag = xpp.getAttributeValue(null, "tag");
					String title = xpp.getAttributeValue(null, "title");
					Route r = new Route();
					r.setTag(tag);
					r.setTitle(title);
					routes.add(r);
				}
				break;
			default://
			}
			eventType = xpp.next();
		}
		return routes;
	}

	/* ONLY a service should call this method */
	//public static Route getRouteDetail(String urlString)
	public static Route getRouteDetail(String  routeTag)
			throws ClientProtocolException, IOException, XmlPullParserException {
		String url = getRestUrlForRouteDetail(routeTag);
		String str = getContentFromUrl(url);
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(str));
		int eventType = xpp.getEventType();

		Route r = null;
		Direction d = null;
		Pathz path = null;
		Stop s = null;
		Pointz po = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (xpp.getName().equalsIgnoreCase("route")) {
					r = new Route();
					r.setTag(xpp.getAttributeValue(null, "tag"));
					r.setTitle(xpp.getAttributeValue(null, "title"));
					r.setColor(xpp.getAttributeValue(null, "color"));
					r.setOppositeColor(xpp.getAttributeValue(null,
							"oppositeColor"));
					r.setLatMin(xpp.getAttributeValue(null, "latMin"));
					r.setLatMax(xpp.getAttributeValue(null, "latMax"));
					r.setLonMin(xpp.getAttributeValue(null, "lonMin"));
					r.setLonMax(xpp.getAttributeValue(null, "lonMax"));
					break;
				} else if (xpp.getName().equalsIgnoreCase("stop")) {
					s = new Stop();
					s.setTag(xpp.getAttributeValue(null, "tag"));
					s.setTitle(xpp.getAttributeValue(null, "title"));
					s.setLat(xpp.getAttributeValue(null, "lat"));
					s.setLon(xpp.getAttributeValue(null, "lon"));
					s.setStopId(xpp.getAttributeValue(null, "stopId"));
					break;
				} else if (xpp.getName().equalsIgnoreCase("direction")) {
					d = new Direction();
					d.setTag(xpp.getAttributeValue(null, "tag"));
					d.setTitle(xpp.getAttributeValue(null, "title"));
					d.setName(xpp.getAttributeValue(null, "name"));
					d.setUseForUI(xpp.getAttributeValue(null, "useForUI"));
					break;
				} else if (xpp.getName().equalsIgnoreCase("path")) {
					// assign to the parent 'route' tag
					path = new Pathz();
					break;
				} else if (xpp.getName().equalsIgnoreCase("point")) {
					po = new Pointz();
					po.setLat(xpp.getAttributeValue(null, "lat"));
					po.setLon(xpp.getAttributeValue(null, "lon"));
					break;
				}
				break;

			case XmlPullParser.END_TAG:
				if (xpp.getName().equalsIgnoreCase("point")) {
					if (po != null && path != null)
						path.getPoints().add(po);
					po = null; // important to reset to null.
					break;
				} 
				else if (xpp.getName().equalsIgnoreCase("path")) {
					// assign to the parent 'route' tag
					if (path != null && r != null)
						r.getPaths().add(path);
					path = null;
					break;
				}

				else if (xpp.getName().equalsIgnoreCase("direction")) {
					if (d != null && r != null)
						r.getDirections().add(d);
					d = null;
					break;
				} 
				else if (xpp.getName().equalsIgnoreCase("stop")) {
					// to know whether this stop tag is child of route or
					// direction. we check if the parent are null or not.
					if (s != null && r != null) {
						if (d == null) {
							r.getStops().add(s);
						} else if (d != null) {
							d.getStops().add(s);
						}
					}
					s = null;
					break;
				} 
				else if (xpp.getName().equalsIgnoreCase("route")) {
					// end of route tag. no need to further process. this route
					// has all the details of its decendents.
					return r;
				}
				break;

			default:
				break;

			}
			eventType = xpp.next();
		}

		return null;
	}

	public static String getRestUrlForRoutelist() {
		return REST_URL_BASE + "&command=routeList";
	}

	public static boolean isRestUrlForRoutelist(String url) {
		return url.matches(".*&command=routeList$");
	}

	public static String getRestUrlForRouteDetail(String routeTag) {
		return REST_URL_BASE + "&command=routeConfig&r=" + routeTag;
	}

	public static boolean isRestUrlForRouteDetail(String url) {
		return url.matches(".*&command=routeConfig.*")
				&& url.matches(".*&r=.*");
	}

	public static String getRestUrlForPredictions(String stopid) {
		return REST_URL_BASE + "&command=predictions&s=" + stopid;
	}

	public static boolean isRestUrlForPredictionswithStopArgument(String url) {
		return url.matches(".*&command=predictions.*")
				&& url.matches(".*&s=.*") && (url.matches(".*&r=") == false);
	}

	public static String getRestUrlForPredictions(String stopid, String routeTag) {
		return REST_URL_BASE + "&command=predictions&s=" + stopid + "&r="
				+ routeTag;
	}

	public static boolean isRestUrlForPredictionswithStopAndRoutetagArguments(
			String url) {
		return url.matches(".*&command=predictions.*")
				&& url.matches(".*&s=.*") && url.matches(".*&r=");
	}

	/*
	 * parameter stops is a list of String in this format 'routetag|stopid' eg.
	 * 1S|2425
	 */
	public static String getRestUrlForPredictionsForMultiStops(
			List<String> routeAndStops) {
		String routeStopArgString = "";
		for (String rs : routeAndStops) {
			routeStopArgString = routeStopArgString + "&stops=" + rs;
		}
		return REST_URL_BASE + "command=predictionsForMultiStops"
				+ routeStopArgString;
	}

	public static boolean isRestUrlForPredictionsForMultiStops(String url) {
		return url.matches(".*&command=predictions.*")
				&& url.matches(".*&stops=.*");
	}

	public static String getRestUrlForSchedule(String routeTag) {
		return REST_URL_BASE + "command=schedule&r=" + routeTag;
	}

	public static boolean isRestUrlForSchedule(String url) {
		return url.matches(".*&command=schedule.*") && url.matches(".*&r=.*");
	}

	public static String getRestUrlForMessageForRoutes(List<String> routeTags) {
		String argString = "";
		for (String routeTag : routeTags) {
			argString = argString + "&r=" + routeTag;
		}
		return REST_URL_BASE + "command=messages" + argString;
	}

	public static boolean isRestUrlForMessageForRoutes(String url) {
		return url.matches(".*&command=messages.*") && url.matches(".*&r=.*");
	}

}
