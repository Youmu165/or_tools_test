package p4_group_8_repo.utils;

import java.io.File;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class xmlGenerator {
    public static void generateVehicleXML(String filePath, List<Map<String, Object>> vehicleRoutes) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element rootElement = doc.createElement("vehicles");
            doc.appendChild(rootElement);

            for (Map<String, Object> vehicle : vehicleRoutes) {
                Element vehicleElement = doc.createElement("vehicle");
                vehicleElement.setAttribute("id", String.valueOf(vehicle.get("vehicle_id")));
                rootElement.appendChild(vehicleElement);

                Element routeElement = doc.createElement("route");
                List<Integer> route = (List<Integer>) vehicle.get("route");
                routeElement.appendChild(doc.createTextNode(route.toString()));
                vehicleElement.appendChild(routeElement);

                Element cargoElement = doc.createElement("cargo");
                int[] cargo = (int[]) vehicle.get("cargo");
                cargoElement.setAttribute("large_boxes", String.valueOf(cargo[0]));
                cargoElement.setAttribute("medium_boxes", String.valueOf(cargo[1]));
                cargoElement.setAttribute("small_boxes", String.valueOf(cargo[2]));
                vehicleElement.appendChild(cargoElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            System.out.println("Vehicle routes XML saved in " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
