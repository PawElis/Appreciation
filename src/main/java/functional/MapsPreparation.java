package functional;

import bean.ConnectorBean;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import structures.Places;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static functional.InformationHandler.getAdvised;

/**
 * Created by Pavel on 06.12.2017.
 * Preparation for Map
 */
public class MapsPreparation {

    public static class Point {
        private float latitude;
        private float longitude;

        Point(float latitude, float longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }

        double getDistance(Point other) {
            return Math.sqrt(Math.pow(this.latitude - other.latitude, 2)
                    + Math.pow(this.longitude - other.longitude, 2));
        }

        private int getNearestPointIndex(List<Point> points) {
            int index = -1;
            double minDist = Double.MAX_VALUE;
            for (int i = 0; i < points.size(); i++) {
                double dist = this.getDistance(points.get(i));
                if (dist < minDist) {
                    minDist = dist;
                    index = i;
                }
            }
            return index;
        }

        private static Point getMean(List<Point> points) {
            float accumX = 0;
            float accumY = 0;
            if (points.size() == 0) return new Point(accumX, accumY);
            for (Point point : points) {
                accumX += point.latitude;
                accumY += point.longitude;
            }
            return new Point(accumX / points.size(), accumY / points.size());
        }

        @Override
        public String toString() {
            return "[" + this.latitude + "," + this.longitude + "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj.getClass() != Point.class) {
                Point other = (Point) obj;
                return this.latitude == other.latitude && this.longitude == other.longitude;
            } else {
                return false;
            }
        }
    }

    // K-Means
    private static List<Point> getDataSet(ArrayList<Places> places) {
        List<Point> dataset = new ArrayList<>();
        for (Places place : places) {
            float latitude = place.getLatitude();
            float longitude = place.getLongitude();
            Point point = new Point(latitude, longitude);
            dataset.add(point);
        }
        return dataset;
    }

    private static List<Point> initializeRandomCenters(int n) {
        List<Point> centers = new ArrayList<>(n);
        float longitudeMin = (float) 29;
        float longitudeMax = (float) 31;
        float latitudeMin = (float) 59.5;
        float latitudeMax = (float) 60.5;
        for (int i = 0; i < n; i++) {
            float latitude = (float) (Math.random() * (latitudeMax - latitudeMin) + latitudeMin);
            float longirude = (float) (Math.random() * (longitudeMax - longitudeMin) + longitudeMin);
            Point point = new Point(latitude, longirude);
            centers.add(point);
        }
        return centers;
    }

    private static List<Point> getNewCenters(List<Point> dataSet, List<Point> centers) {
        List<List<Point>> clusters = new ArrayList<>(centers.size());
        for (int i = 0; i < centers.size(); i++) {
            clusters.add(new ArrayList<>());
        }
        for (Point data : dataSet) {
            int index = data.getNearestPointIndex(centers);
            clusters.get(index).add(data);
        }
        List<Point> newCenters = new ArrayList<>(centers.size());
        for (List<Point> cluster : clusters) {
            newCenters.add(Point.getMean(cluster));
        }
        return newCenters;
    }

    private static double getDistance(List<Point> oldCenters, List<Point> newCenters) {
        double accuseDist = 0;
        for (int i = 0; i < oldCenters.size(); i++) {
            double dist = oldCenters.get(i).getDistance(newCenters.get(i));
            accuseDist += dist;
        }
        return accuseDist;
    }

    private static List<Point> kMeans(List<Point> centers, List<Point> dataSet) {
        boolean converged;
        do {
            List<Point> newCenters = getNewCenters(dataSet, centers);
            double dist = getDistance(centers, newCenters);
            centers = newCenters;
            converged = dist == 0;
        } while (!converged);
        centers = notNullCenters(centers);
        return centers;
    }

    private static List<Point> notNullCenters(List<Point> centers) {
        List<Point> newCenters = new ArrayList<>();
        for (Point center : centers) {
            if ((center.latitude > 0.01) && (center.longitude > 0.01)) {
                newCenters.add(center);
            }
        }
        return newCenters;
    }

    static List<Point> doKMeans(ArrayList<Places> places, int k) {
        List<Point> dataSet = getDataSet(places);
        List<Point> centers = initializeRandomCenters(k);
        return kMeans(centers, dataSet);
    }

    private static ArrayList<Places> doKMeansToCl(ArrayList<Places> places, int k) {
        if (places.size() > k) {
            List<Point> dataSet = getDataSet(places);
            List<Point> centers = initializeRandomCenters(k);
            return convertToPlaces(kMeans(centers, dataSet));
        }
        return places;
    }

    private static ArrayList<Places> convertToPlaces(List<Point> centers) {
        ArrayList<Places> center = new ArrayList<>();
        for (Point point : centers) {
            center.add(new Places(point.latitude, point.longitude));
        }
        return center;
    }

    // Geo Objects
    public static void removeAllAttr(HttpSession session) {
        session.removeAttribute("places");
        session.removeAttribute("hexagons");
        session.removeAttribute("nameVoc");
        session.removeAttribute("pl");
        session.removeAttribute("colors");
        session.removeAttribute("weight");
    }

    public static void setAllUsersAttr(HttpSession session, ConnectorBean connectorBean, HttpServletRequest request) {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        String redirectUrl = "http://localhost/account";
        UserActor actor = InformationHandler.getUserActor(request.getParameter("code"), vk, redirectUrl);

        ArrayList<Integer> list = new ArrayList<>();
        list.add(actor.getId());
        ArrayList<String> s = InformationHandler.getInformAboutUser(actor, vk, list);

        session.setAttribute("hash", actor.hashCode());
        session.setAttribute("token", actor.getAccessToken());
        session.setAttribute("id", actor.getId());
        session.setAttribute("name", s.get(0));
        session.setAttribute("photo", s.get(1));
        ArrayList<Places> places = connectorBean.getPlaces();
        int numOfCl = 500;
        ArrayList<Places> centers = doKMeansToCl(places, numOfCl);
        session.setAttribute("places", centers);

        int numOfHex = 70;
        float latitudeMax = (float) 64;
        float latitudeMin = (float) 59;
        float longitudeMax = (float) 33;
        float longitudeMin = (float) 28;
        ArrayList<ArrayList<MapsPreparation.Point>> allPoints = MapsPreparation
                .getAllImportantHexagons(numOfHex, longitudeMin, latitudeMin, longitudeMax, latitudeMax, places);
        session.setAttribute("hexagons", allPoints);

        ArrayList<ArrayList<Integer>> weight = InformationHandler.getAppreciationForAllVoc(connectorBean);
        session.setAttribute("weight", weight);
        ArrayList<String> nameVoc = InformationHandler.getAllNameVoc(connectorBean);
        session.setAttribute("nameVoc", nameVoc);
        ArrayList<String> colorsVoc = InformationHandler.getColorsVoc(connectorBean);
        session.setAttribute("colors", colorsVoc);
        ArrayList<ArrayList<MapsPreparation.Point>> pl = InformationHandler.getPlaces(connectorBean);
        int numOfVoc = connectorBean.numOfVocabulary();
        for (int i = 0; i < numOfVoc; i++) {
            session.setAttribute("pl" + i, pl.get(i));
        }

        ArrayList<MapsPreparation.Point> placeAd = getAdvised(connectorBean, actor);
        session.setAttribute("pl6", placeAd);
    }

    public static void setAllAttr(HttpSession session, ConnectorBean connectorBean) {
        ArrayList<Places> places = connectorBean.getPlaces();
        int numOfCl = 500;
        ArrayList<Places> centers = doKMeansToCl(places, numOfCl);
        session.setAttribute("places", places);

        int n = 70;
        float latitudeMax = (float) 64;
        float latitudeMin = (float) 59;
        float longitudeMax = (float) 33;
        float longitudeMin = (float) 28;
        ArrayList<ArrayList<MapsPreparation.Point>> allPoints = MapsPreparation
                .getAllImportantHexagons(n, longitudeMin, latitudeMin, longitudeMax, latitudeMax, places);
        session.setAttribute("hexagons", allPoints);

        ArrayList<ArrayList<Integer>> weight = InformationHandler.getAppreciationForAllVoc(connectorBean);
        session.setAttribute("weight", weight);
        ArrayList<String> nameVoc = InformationHandler.getAllNameVoc(connectorBean);
        session.setAttribute("nameVoc", nameVoc);
        ArrayList<String> colorsVoc = InformationHandler.getColorsVoc(connectorBean);
        session.setAttribute("colors", colorsVoc);
        ArrayList<ArrayList<MapsPreparation.Point>> pl = InformationHandler.getPlaces(connectorBean);
        int numOfVoc = connectorBean.numOfVocabulary();
        for (int i = 0; i < numOfVoc; i++) {
            session.setAttribute("pl" + i, pl.get(i));
        }
    }

    private static ArrayList<Point> getHexagonForCenter(int n, float latitude1, float latitude2, Point center) {
        float step = (latitude2 - latitude1) / (3 * n);
        Point point1 = new Point(center.getLatitude() + step, center.getLongitude() - step * 3 / 2);
        Point point2 = new Point(center.getLatitude() + step, center.getLongitude() + step * 3 / 2);
        Point point3 = new Point(center.getLatitude(), center.getLongitude() + step * 3);
        Point point4 = new Point(center.getLatitude() - step, center.getLongitude() + step * 3 / 2);
        Point point5 = new Point(center.getLatitude() - step, center.getLongitude() - step * 3 / 2);
        Point point6 = new Point(center.getLatitude(), center.getLongitude() - step * 3);

        ArrayList<Point> hexagon = new ArrayList<>(6);
        hexagon.add(point1);
        hexagon.add(point2);
        hexagon.add(point3);
        hexagon.add(point4);
        hexagon.add(point5);
        hexagon.add(point6);
        return hexagon;
    }

    private static ArrayList<Point> centers(int n, float longitude1, float latitude1, float longitude2, float latitude2) {
        float step = (latitude2 - latitude1) / (3 * n);
        int m = Math.round((longitude2 - longitude1) / step);
        ArrayList<Float> centerLat = new ArrayList<>();
        ArrayList<Float> centerLon = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                centerLat.add(latitude1 + step * i);
                centerLon.add(longitude1 + step * j);
            }
        }
        ArrayList<Point> centerArr = new ArrayList<>();
        for (int i = 0; i < centerLat.size(); i++) {
            centerArr.add(new Point(centerLat.get(i), centerLon.get(i)));
        }
        return centerArr;
    }

    private static ArrayList<Point> importantCenters(ArrayList<Point> centers, ArrayList<Places> places, float step) {
        ArrayList<Point> importantCenters = new ArrayList<>();
        for (Point point : centers) {
            for (Places place : places) {
                if (Math.sqrt(Math.pow(point.getLongitude() - place.getLongitude(), 2) + Math.pow(point.getLatitude() - place.getLatitude(), 2)) < step) {
                    importantCenters.add(point);
                }
            }
        }
        return importantCenters;
    }

    private static ArrayList<ArrayList<Point>> getAllImportantHexagons(int n, float longitude1, float latitude1, float longitude2, float latitude2, ArrayList<Places> places) {
        float step = (latitude2 - latitude1) / (3 * n);
        ArrayList<Point> importantCantersArr = importantCenters(centers(n, longitude1, latitude1, longitude2, latitude2), places, step);
        ArrayList<ArrayList<Point>> allImpHexagons = new ArrayList<>();
        for (Point point : importantCantersArr) {
            allImpHexagons.add(getHexagonForCenter(n, latitude1, latitude2, point));
        }
        return allImpHexagons;
    }
}
