package ecologylab.bigsemantics.html.standalone;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import org.w3c.dom.Node;

import ecologylab.bigsemantics.html.DOMWalkInformationTagger;
import ecologylab.net.ParsedURL;


/**
 * 
 * @author eunyee
 *
 */
@Deprecated
public class GenerateSVMData extends OldHTMLDOMParser
{
    public void pprint(org.w3c.dom.Document doc, OutputStream out, ParsedURL purl, String mainPartitionID)
    {
//        Out o = new OutJavaImpl(this.getConfiguration(), null);
        Node document = null;


//        o.state = StreamIn.FSM_ASCII;
//        o.encoding = configuration.CharEncoding;

  //      if (out != null)
  //      {
        	// Instantiate PPrint constructor that connects to combinFormation
        DOMWalkInformationTagger pprint = new DOMWalkInformationTagger(purl, null);
        
        // To generate SVM data 
        pprint.setPartitionID(mainPartitionID);
        
        FileOutputStream outFile;
		try {
			outFile = new FileOutputStream("svmData.csv", true);
			pprint.setFileOutputStream(outFile);
			StringWriter o = new StringWriter();
	        
//	        o.out = out;
//	        if (configuration.xmlTags)
//	            pprint.printXMLTree(o, (short)0, 0, null, document);
//	        else
	            pprint.tagTree(document);
	        
	        outFile.close();
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	          	             
       

   

    }
/*    
	protected String getContentBody(URL labelFile)
	{
		try 
		{
			DocumentState ds = (DocumentState) ElementState.translateFromXML(labelFile, TranslationScope.get("collectionBrowseServlet", "collectionBrowseServlet"));
			if( ds!=null )
			{
				//totalLabeledDocument++;
				
				PartitionState partitionState = ds.getPartitionSet().get(0);
			
				String mainPartitionTag_ID = partitionState.getTag_id();
System.out.println("   mainPartitionTag_ID:" + mainPartitionTag_ID);

				return mainPartitionTag_ID;

			}
		} 
		catch (XMLTranslationException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static void main(String args[])
	{
		GenerateSVMData cbr = new GenerateSVMData();
		URL url;
		try 
		{
			  File ff = new File( "folderList.txt" );  
			
			  InputStream ii = new FileInputStream(ff);
			  BufferedReader myInput 	= new BufferedReader(new InputStreamReader(ii));

			  String temp = null;
			  while( (temp=myInput.readLine())!=null )
			  {
				
				String urlString = "http://csdll.cs.tamu.edu:9080/TestCollections/websites/NewsContent/" + temp.trim() + "/";
				String labelURLStr = urlString + "label.xml";
				
				url = new URL(urlString);
				System.out.println(urlString);
				
				System.out.println("\n\n" + urlString );				
				URL labelURL = new URL(labelURLStr);
				try
				{
					if( (labelURL!=null) && (labelURL.openConnection()!=null) && (labelURL.getContent()!=null) )
					{
						String returnVal=cbr.getContentBody(labelURL);
						if( returnVal != null )
						{
							InputStream in = url.openConnection().getInputStream();
							cbr.pprint( cbr.parseDOM(in, null), null, urlString, returnVal); 
						}
					}
					else 
						System.out.println("NO LABEL for this document");

				}
				catch(FileNotFoundException e)
				{
					continue;
				}
				
											
				System.out.println("\n");
			  }

			
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}
*/	
}