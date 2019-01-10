package mil.army.drrsa.projection.jfw.wsclient.da.ref;

import mil.army.drrsa.projection.jfw.wsclient.DVLClient;

import mil.ces.metadata.des.sd.jpes.jfw.wsdl._4_0_0.*;
import mil.ces.metadata.des.sd.jpes.jfw.plan._4_0_0.*;
import mil.ces.metadata.des.sd.jpes.jfw.rr._4_0_0.GetAllTudetRequest;
import mil.ces.metadata.des.sd.jpes.jfw.rr._4_0_0.GetAllTudetResponse;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.math.BigInteger;

import javax.servlet.*;
import javax.servlet.http.*;

public class TudetDA extends DVLClient{

	public void init(ServletConfig config) throws ServletException{	
		super.init(config);
		//SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		DVL port = connectToDVL();
		
		boolean errorFound = false;
		
		int _offset = 0;
		int _itemsPerPage = 100;
		String _queryCacheKey = "";
		
		int numReceived = 0;
		
		String errorMsg = null; 
		
		
		while (errorFound == false){
			
			GetAllTudetRequest getAllTudetRequest = new GetAllTudetRequest();
			
			getAllTudetRequest.setItemsPerPage(BigInteger.valueOf(_itemsPerPage));
			
			if (numReceived > 0){
				getAllTudetRequest.setQueryCacheKey(_queryCacheKey);
				getAllTudetRequest.setOffset(BigInteger.valueOf(_offset));
			}
			
			try{
				GetAllTudetResponse getAllTudetResponse = port.getAllTudet(getAllTudetRequest);
				
				if ( (getAllTudetResponse == null) || (getAllTudetResponse.getTUDET() == null) ){
					break;  //stop requesting data
				}
				
				System.out.println("Got data back from the service.");

				List<TUDET> ccRefs = getAllTudetResponse.getTUDET();
								
				if ( (ccRefs == null) || (ccRefs.size() == 0)){
					System.out.println("The list from the response is empty.");
					break; //stop requesting data
				}	
					
				System.out.println("The list from the response has " + ccRefs.size() + " objects.");									
				
				numReceived += ccRefs.size();
				
				if ( ccRefs.size() < _itemsPerPage ){
					break; //stop requesting data
				}else{
					_queryCacheKey = getAllTudetResponse.getQueryCacheKey();
					_offset = _offset + _itemsPerPage;		
				}					
				
			}catch(com.sun.xml.ws.addressing.model.MissingAddressingHeaderException mis){
				errorFound = true;
				errorMsg = "Error occurred MissingAddressingHeaderException=" + mis;
			}catch(DefaultSoapWsFault defaultSoapWsFault){
				errorFound = true;
				errorMsg = "Error occurred DefaultSoapWsFault: " + defaultSoapWsFault; 
			}catch(Exception ex){
				errorFound = true;
				errorMsg = "Error occurred Exception: " + ex;
			}
		}//while
		
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		
		if (errorMsg != null){
		  out.println("<h1>" + errorMsg + "</h1>");
		
		}else{
		  out.println("<h1>" + "Saved " + numReceived + " TUDET objects." + "</h1>");
		}
		
	}

	public void destroy()
	{
	// do nothing.
	}
	
}

