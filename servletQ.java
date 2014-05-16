
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;



import org.xml.sax.InputSource;



import java.lang.String;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.URL;
import java.net.URLConnection;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.json.*;
import javax.xml.parsers.*;
//import org.w3c.dom.*;
//import org.json.JSONObject;

public class servletQ extends HttpServlet {

	 public void doGet(HttpServletRequest request,HttpServletResponse response)
	    throws IOException, ServletException {

	    String[] day = new String[5];
			String[] low = new String[5];
			String[] high = new String[5];
			String[] weather = new String[5];


        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no_cache");



        String cityname = request.getParameter("locName");
        //cityname = cityname.replaceAll("\\s{1,}","+");

        String typename = request.getParameter("locType");
        String unit = request.getParameter("tempUnit");

        cityname = new String(cityname.getBytes("iso-8859-1"),"UTF-8");
			cityname = java.net.URLEncoder.encode(cityname,"UTF-8");
			typename = new String(typename.getBytes("iso-8859-1"),"UTF-8");
			typename = java.net.URLEncoder.encode(typename,"UTF-8");
       String urlstring = "http://cs-server.usc.edu:27603/newphp.php?locName="+cityname+"&locType="+typename+"&tempUnit="+unit;
		//String urlstring = "http://cs-server.usc.edu:27603/newphp.php?location="+cityname+"&type="+typename+"&unit="+unit;
		//System.out.println(urlstring);
		URL url = new URL(urlstring);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setAllowUserInteraction(false);
		InputStream urlStream = url.openStream();// urlStream store the return
													// XML file

		String xmlStr = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(urlStream));
		String str = "";
		while ((str = br.readLine()) != null) {
			xmlStr += str;
		}

		 //SAXBuilder builder = new SAXBuilder(false);

		// JDOM parse the XML file
		Document doc = null;
		//DocumentBuilder dBuilder;
		// catch the error
		try {
			//DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //dBuilder = dbf.newDocumentBuilder();
			StringReader reader = new StringReader(xmlStr);
		    	 InputSource source = new InputSource(reader);
		    	 source.setEncoding("utf-8");
				 SAXBuilder builder = new SAXBuilder(false);
				 doc = builder.build(source);



			//InputStream parsedXmlStr = new ByteArrayInputStream(xmlStr.getBytes());

			//doc = dBuilder.parse(parsedXmlStr);

		} catch (Exception f) {
			System.err.println(f);
		}

		Element root=doc.getRootElement();



		String feed = root.getChildText("feed");
        String link = root.getChildText("link");
        String img = root.getChildText("img");
        List list = root.getChildren("forecast");
        Element node = null;

        for (int i = 0; i < 5; i++)
        {
         node = (Element) list.get(i);
         day[i] = node.getAttributeValue("day");
         high[i] = node.getAttributeValue("high");
         low[i] = node.getAttributeValue("low");
          weather[i] = node.getAttributeValue("text");
         }

		JSONArray jsonArr = new JSONArray();

		list = root.getChildren("location");
          node = (Element) list.get(0);
         String city = node.getAttributeValue("city");
         String region = node.getAttributeValue("region");
         String country = node.getAttributeValue("country");

         list = root.getChildren("units");
          node = (Element) list.get(0);
         String temperature = node.getAttributeValue("temperature");

         list = root.getChildren("condition");
         node = (Element) list.get(0);
        String text = node.getAttributeValue("text");
         String temp = node.getAttributeValue("temp");







		//NodeList forecastlist = weatherdoc.getElementsByTagName("forecast");

		// List
		// hotellist=hoteldoc.getRootElement().getChild("hotels").getChildren();
		for (int i = 0; i < 5; i++) {
			JSONObject jsonNode = new JSONObject();
			jsonNode.put("text", weather[i]);
			jsonNode.put("high", high[i]);
			jsonNode.put("day", day[i]);
			jsonNode.put("low", low[i]);

			jsonArr.put(jsonNode);
		}



		JSONObject forecastJson = new JSONObject();
		forecastJson.put("forecast", jsonArr);

		JSONObject conditionJson = new JSONObject();
		conditionJson.put("text", text);
		conditionJson.put("temp",temp);
		forecastJson.put("condition",conditionJson);

		JSONObject locationJson = new JSONObject();
		locationJson.put("region", region);
		locationJson.put("country",country);
		locationJson.put("city",city);
		forecastJson.put("location",locationJson);

		forecastJson.put("link",link);
		forecastJson.put("img",img);
		forecastJson.put("feed",feed);


		JSONObject unitsJson = new JSONObject();
		unitsJson.put("temperature", temperature);
		forecastJson.put("units",unitsJson);

		JSONObject weatherJson = new JSONObject();
		weatherJson.put("weather",forecastJson);


 String front = "{\n  \"weather\":{\n    \"forecast\":[\n";
				 for(int i =0;i<5;i++){
					 front+="      {\n";
					 front +="        \"text\":"+"\""+weather[i]+"\",\n";
					 front +="        \"high\":"+high[i]+",\n";
					 front +="        \"day\":"+"\""+day[i]+"\",\n";
					 front +="        \"low\":"+low[i];
					 if(i!=4){
						 front+="\n      },\n";
					 }
					 else{
						 front+="\n      }\n";
					 }
				 }
				 front +="    ],\n";
				 front +="    \"condition\":{\n";
				 front +="      \"text\":"+"\""+text+"\",\n";
				 front +="      \"temp\":"+temp;
				 front +="\n    },\n";
				 front +="    \"location\":{\n";
				 front +="      \"region\":"+"\""+region+"\",\n";
				 front +="      \"country\":"+"\""+country+"\",\n";
				 front +="      \"city\":"+"\""+city+"\"";
				 front +="\n    },\n";
				 front +="    \"link\":"+"\""+link+"\",\n";
				 front +="    \"img\":"+"\""+img+"\",\n";
				 front +="    \"feed\":"+"\""+feed+"\",\n";
				 front +="    \"units\":{\n";
				 front +="      \"temperature\":"+"\""+temperature+"\"";
				 front +="\n    }\n  }\n}";


		PrintWriter out = response.getWriter();
		out.println(weatherJson.toString());
		//out.write(front);
	}

}
