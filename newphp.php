<?php 
Header('Content-type: text/xml');
       $location= $_GET["locName"];
	    $type= $_GET["locType"];
		
		//$location="80028";
		//$unit="F";
		//$type="Zip";
		//$location = "Pasadena";
		//$type = "city";
		$unit=$_GET["tempUnit"];
		//Print $type;
        if($type=="Zip")
		{$url="http://where.yahooapis.com/v1/concordance/usps/".urlencode($location)."?appid=<e9bm1v_V34G3bYv77JucPG8VLk4mOHDtko4wzMNDnJgwokNVM4Zsi68ulky.Jt.EJg-->";
		
		
		}
		
		else if($type=="city")
		{
		   $location_org=$location;
		   //$location=urlencode($location);
		   $url="http://where.yahooapis.com/v1/places\$and(.q('".urlencode($location)."'),.type(7));start=0;count=5?appid=[e9bm1v_V34G3bYv77JucPG8VLk4mOHDtko4wzMNDnJgwokNVM4Zsi68ulky.Jt.EJg--]";
		   
		}
        		
           $content = @file_get_contents($url);
		   
		   if($content==false)
		   { echo "";
      		
		     //return 0;
		   }
           
		   $match_result=preg_match_all('/<woeid>(\d+)<\/woeid>/',$content,$woeid,PREG_PATTERN_ORDER);
		   if(!$match_result)
		   {   echo "";
				  // return 0;
		   }
		   
		   
		   
		   
		   
				if(count($woeid[1])==0)
				{echo "";}
				  //return 0;}
			$flag=0;//Reset the flag value
		   //for($i = 0; $i < count($woeid[1]);$i++)
		   
		   if($unit=="F")	
		       $new_url="http://weather.yahooapis.com/forecastrss?w=".$woeid[1][0]."&u=f";
			    
			   
			   else
			   $new_url="http://weather.yahooapis.com/forecastrss?w=".$woeid[1][0]."&u=c";	
			    
				$feed= $new_url;
				$xml=simplexml_load_file($new_url);
			   if($xml->channel->title=="Yahoo! Weather - Error")
			    {echo "<\weather>";
		         return 0;}
			    
				preg_match_all('/src="(.*?)"/',$xml->channel->item->description,$image,PREG_PATTERN_ORDER);
			 
			  $text=$xml->channel->item->children('yweather',true)->condition->attributes()->text;
			  $img=$image[1][0];
			  
			  
			 
					    $temp=$xml->channel->item->children('yweather',true)->condition->attributes()->temp;
					    $temp_unit=$xml->channel->children('yweather',true)->units->attributes()->temperature;
		      
			 
			  $city_out=$xml->channel->children('yweather',true)->location->attributes()->city;
			 
			  $region_out=$xml->channel->children('yweather',true)->location->attributes()->region;
			 
			  $country_out=$xml->channel->children('yweather',true)->location->attributes()->country;
			 
               $lat_out=$xml->channel->item->children('geo',true)->lat;
			  
			  $link_out=$xml->channel->link;
			   // header("Content-type: text/xml");
			  
			  $xml_out = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
	$xml_out .= "<weather>"."\n";
	$xml_out .="<feed>".htmlentities($feed)."</feed>";
	$xml_out .="<link>".htmlentities($link_out)."</link>";
	$xml_out .="<location city = \"$city_out\" region = \"$region_out\" country = \"$country_out\"/>";
	$xml_out .="<units temperature = \"$temp_unit\" />";
	$xml_out .="<condition text = \"$text\" temp = \"$temp\"/>";
	$xml_out .="<img>". $img."</img>";
	foreach ($xml -> channel ->item-> children('yweather', TRUE) -> forecast as $condition) {

	$day = $condition -> attributes() -> day;
	$low = $condition -> attributes() -> low;

	$high = $condition -> attributes() -> high;
	$textFor = $condition -> attributes() -> text;

	//echo "<forecast	day=\"" . $day . "\" low=\"" . $low . "\" high=\"" . $high . "\" text=\"" . $text . "\"/>";
    $xml_out .="<forecast day = \"$day\" low = \"$low\" high = \"$high\" text = \"$textFor\"/>";
     }
	
	
	
	
 	$xml_out.= "</weather>";
	echo $xml_out;
	
			  
			
 

        
       
 ?>
  
