package com.thuptencho.torontotransitbus.backgroundservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.thuptencho.torontotransitbus.models.Direction;
import com.thuptencho.torontotransitbus.models.Pathz;
import com.thuptencho.torontotransitbus.models.Pointz;
import com.thuptencho.torontotransitbus.models.Prediction;
import com.thuptencho.torontotransitbus.models.Route;
import com.thuptencho.torontotransitbus.models.Stop;
import com.thuptencho.torontotransitbus.utilities.MyLogger;

public class RestClient {
	private static final String REST_URL_BASE = "http://webservices.nextbus.com/service/publicXMLFeed?a=ttc";

	private static InputStream getContentFromUrl(final String urlString) throws ClientProtocolException, IOException {
		MyLogger.log(urlString);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpUriRequest request = new HttpGet(urlString);
		InputStream iStream = null;
		try {
			HttpResponse response = httpClient.execute(request);
			iStream = response.getEntity().getContent();
		} finally {
			//httpClient.getConnectionManager().shutdown();
		}
		return iStream;
	}

	/*
	 * ONLY a service should call this method ..should call insert/update on
	 * provider in the end of this method
	 */
	public static List<Route> getRoutes() throws XmlPullParserException, IOException {

		String urlString = RestClient.getRestUrlForRoutelist();

		InputStream iStream = RestClient.getContentFromUrl(urlString);

		List<Route> routes = new ArrayList<Route>();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(iStream, null);

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
					r.mTag = tag;
					r.mTitle = title;
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
	// public static Route getRouteDetail(String urlString)
	public static Route getRouteDetail(String routeTag) throws ClientProtocolException, IOException,
					XmlPullParserException {
		String url = getRestUrlForRouteDetail(routeTag);
		InputStream iStream = getContentFromUrl(url);
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(iStream, null);
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
					r.mTag = xpp.getAttributeValue(null, "tag");
					r.mTitle = xpp.getAttributeValue(null, "title");
					r.nColor = xpp.getAttributeValue(null, "color");
					r.mOppositeColor = xpp.getAttributeValue(null, "oppositeColor");
					r.mLatMin = xpp.getAttributeValue(null, "latMin");
					r.mLatMax = xpp.getAttributeValue(null, "latMax");
					r.mLonMin = xpp.getAttributeValue(null, "lonMin");
					r.mLonMax = xpp.getAttributeValue(null, "lonMax");
					break;
				}
				else if (xpp.getName().equalsIgnoreCase("stop")) {
					s = new Stop();
					s.mTag = xpp.getAttributeValue(null, "tag");
					s.mTitle = xpp.getAttributeValue(null, "title");
					s.mLat = xpp.getAttributeValue(null, "lat");
					s.mLon = xpp.getAttributeValue(null, "lon");
					s.mStopId = xpp.getAttributeValue(null, "stopId");
					break;
				}
				else if (xpp.getName().equalsIgnoreCase("direction")) {
					d = new Direction();
					d.mTag = xpp.getAttributeValue(null, "tag");
					d.mTitle = xpp.getAttributeValue(null, "title");
					d.mName = xpp.getAttributeValue(null, "name");
					d.mUseForUI = xpp.getAttributeValue(null, "useForUI");
					break;
				}
				else if (xpp.getName().equalsIgnoreCase("path")) {
					// assign to the parent 'route' tag
					path = new Pathz();
					break;
				}
				else if (xpp.getName().equalsIgnoreCase("point")) {
					po = new Pointz();
					po.mLat = xpp.getAttributeValue(null, "lat");
					po.mLon = xpp.getAttributeValue(null, "lon");
					break;
				}
				break;

			case XmlPullParser.END_TAG:
				if (xpp.getName().equalsIgnoreCase("point")) {
					if (po != null && path != null) path.mPoints.add(po);
					po = null; // important to reset to null.
					break;
				}
				else if (xpp.getName().equalsIgnoreCase("path")) {
					// assign to the parent 'route' tag
					if (path != null && r != null) r.mPaths.add(path);
					path = null;
					break;
				}

				else if (xpp.getName().equalsIgnoreCase("direction")) {
					if (d != null && r != null) r.mDirections.add(d);
					d = null;
					break;
				}
				else if (xpp.getName().equalsIgnoreCase("stop")) {
					// to know whether this stop tag is child of route or
					// direction. we check if the parent are null or not.
					if (s != null && r != null) {
						if (d == null) {
							r.mStops.add(s);
						}
						else if (d != null) {
							d.mStops.add(s);
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

	public static List<Prediction> getPredictions(String routeTag, String stopTag) throws ClientProtocolException,
					IOException, XmlPullParserException {
		String url = getRestUrlForPredictions(stopTag, routeTag);
		InputStream predictionStream = RestClient.getContentFromUrl(url);
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(predictionStream, null);
		int eventType = xpp.getEventType();
		List<Prediction> predictions = new ArrayList<Prediction>();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if (xpp.getName().equalsIgnoreCase("prediction")) {
					Prediction pre = new Prediction();
					pre.epochTime = xpp.getAttributeValue(null, "epochTime");
					pre.seconds = xpp.getAttributeValue(null, "seconds");
					pre.minutes = xpp.getAttributeValue(null, "minutes");
					pre.isDeparture = xpp.getAttributeValue(null, "isDeparture");
					pre.affectedByLayover = xpp.getAttributeValue(null, "affectedByLayover");
					pre.branch = xpp.getAttributeValue(null, "branch");
					pre.dirTag = xpp.getAttributeValue(null, "dirTag");
					pre.vehicle = xpp.getAttributeValue(null, "vehicle");
					pre.block = xpp.getAttributeValue(null, "block");
					pre.tripTag = xpp.getAttributeValue(null, "tripTag");
					predictions.add(pre);
				}
				break;

			default:
				break;
			}
			eventType = xpp.next();

		}

		return predictions;
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
		return url.matches(".*&command=routeConfig.*") && url.matches(".*&r=.*");
	}

	public static boolean isRestUrlForPredictionswithStopArgument(String url) {
		return url.matches(".*&command=predictions.*") && url.matches(".*&s=.*") && (url.matches(".*&r=") == false);
	}

	public static String getRestUrlForPredictions(String stopTag, String routeTag) {
		return REST_URL_BASE + "&command=predictions&s=" + stopTag + "&r=" + routeTag;
	}

	public static boolean isRestUrlForPredictionswithStopAndRoutetagArguments(String url) {
		return url.matches(".*&command=predictions.*") && url.matches(".*&s=.*") && url.matches(".*&r=");
	}

	/*
	 * parameter stops is a list of String in this format 'routetag|stopid' eg.
	 * 1S|2425
	 */
	public static String getRestUrlForPredictionsForMultiStops(List<String> routeAndStops) {
		String routeStopArgString = "";
		for (String rs : routeAndStops) {
			routeStopArgString = routeStopArgString + "&stops=" + rs;
		}
		return REST_URL_BASE + "command=predictionsForMultiStops" + routeStopArgString;
	}

	public static boolean isRestUrlForPredictionsForMultiStops(String url) {
		return url.matches(".*&command=predictions.*") && url.matches(".*&stops=.*");
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
