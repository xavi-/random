/*
  +---------------------------------------------------------------------------+
  | Facebook Development Platform Java Client                                 |
  +---------------------------------------------------------------------------+
  | Copyright (c) 2007 Facebook, Inc.                                         |
  | All rights reserved.                                                      |
  |                                                                           |
  | Redistribution and use in source and binary forms, with or without        |
  | modification, are permitted provided that the following conditions        |
  | are met:                                                                  |
  |                                                                           |
  | 1. Redistributions of source code must retain the above copyright         |
  |    notice, this list of conditions and the following disclaimer.          |
  | 2. Redistributions in binary form must reproduce the above copyright      |
  |    notice, this list of conditions and the following disclaimer in the    |
  |    documentation and/or other materials provided with the distribution.   |
  |                                                                           |
  | THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR      |
  | IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES |
  | OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.   |
  | IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,          |
  | INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT  |
  | NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, |
  | DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY     |
  | THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT       |
  | (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF  |
  | THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.         |
  +---------------------------------------------------------------------------+
  | For help with this library, contact developers-help@facebook.com          |
  +---------------------------------------------------------------------------+
 */

package com.facebook.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import edu.stanford.ejalbert.BrowserLauncher;

public class ExampleClient 
{
	/**
	 * Test main method, just try to get an auth token, which doesn't require a
	 * web browser for the user to log in or anything.
	 */
	public static void main(String[] args) throws Exception 
	{
		FileInputStream fis = new FileInputStream("settings.conf");
		Properties props = new Properties();
		props.load(fis);

		FacebookRestClient client =
			new FacebookRestClient(props.getProperty("api_key"), props.getProperty("secret"));
		String desktop = props.getProperty("desktop");

		if (null != desktop && !"0".equals(desktop))
			client.setIsDesktop(true);

		String auth = client.auth_createToken();
		BrowserLauncher browserLauncher = new BrowserLauncher(null);
		browserLauncher.openURLinBrowser(props.getProperty("login_url") + "&api_key=" +
				props.getProperty("api_key") + "&auth_token=" + auth);
//		System.out.println("hit enter after you have logged into FB");
//		System.in.read();
//		Thread.sleep(5000); // in ms

		System.out.println("Logging in...");
		client.auth_getSession(auth);

		System.out.println("Logged in");
		List<String> lstUID = new ArrayList<String>();
		List<String> lstGID = new ArrayList<String>();
		
//		lstUID.add(Integer.valueOf(14500847));
//		http://www.facebook.com/addfriend.php?id=XXXXX
//		http://mit.facebook.com/profile.php?id=710602
//		FacebookRestClient.printDom(client.users_getInfo(lstUID, EnumSet.of(ProfileField.AFFILIATIONS, ProfileField.STATUS)), "   ");
			
		//*
		try
		{
			FileReader fstream = new FileReader("C://post-fb-names.txt");
			BufferedReader in = new BufferedReader(fstream);
			
			String line;
			
			while((line = in.readLine()) != null)
			{
				lstUID.add(line);
			}
			
			in.close();
			
			
			fstream = new FileReader("C://post-fb-groups.txt");
			in = new BufferedReader(fstream);
			
			while((line = in.readLine()) != null)
			{
				lstGID.add(line);
			}
			
			System.out.println("Number of groups: " +  lstGID.size());
			
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Number of file UID: " + lstUID.size());
		/*
		ArrayList<String> lstSex = new ArrayList<String>(lstUID.size()); 
		
		for(int a = 0; a < lstUID.size();)
		{
			ArrayList<Integer> lstInts = new ArrayList<Integer>(2000);
			
			for(int b = 0; b < 2000 && a < lstUID.size(); b++, a++)
			{
				lstInts.add(Integer.parseInt(lstUID.get(a)));
			}
			
			Document tempDoc = client.users_getInfo(lstInts, EnumSet.of(ProfileField.SEX));
			NodeList tempLst = tempDoc.getElementsByTagName("user");
			
			
			for(int b = 0; b < tempLst.getLength(); b++) 
			{
				NodeList tempMap = tempLst.item(b).getChildNodes();
				
				lstSex.add(tempMap.item(0).getTextContent() 
						+ ":" + tempMap.item(1).getTextContent());		
			}
			
			System.out.println("Lenght: " + tempLst.getLength());
		}
		
		
		try
		{
			// Create file 
			FileWriter fstream = new FileWriter("C://post-fb-sexes.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			System.out.println("Writing names to file...");
			
			for(String sex: lstSex)
			{
				out.write(sex);
				out.newLine();
			}
			
			System.out.println("Done writing names to file...");
			
			//Close the output stream
			out.close();
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}//*/
		
		System.out.println("Done labeling sexes...");
		
		if(false) return;
		
		int keepGoing = 0;
		int groupsPerCall = 4;
		int namesPerCall = 20;
		int name = 0;		
		
		long startTime = System.currentTimeMillis();
		long totalTime = ((5 * 3600) + (0 * 60) + 0) * 1000;
		
		while(keepGoing < 15000 && totalTime > System.currentTimeMillis() - startTime
				&& name < lstUID.size())
		{
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ UIDs:" + lstUID.size() 
					+ "; GIDs: " + lstGID.size() + "; Name: " + name + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("Time left (minutes): "
					+ (totalTime - (System.currentTimeMillis() - startTime)) / 60000.0);

			System.out.println("Wating...");
			Thread.sleep(1000);
			System.out.println("Done wating");
			
			try
			{
				StringBuilder tempUIDs = new StringBuilder("(");
				
				for(int i = 0; i < namesPerCall && name < lstUID.size(); i++)
				{
					String strUID = lstUID.get(name++);
										
					if(i != 0) tempUIDs.append(", ");
										
					tempUIDs.append(strUID);
				}

				tempUIDs.append(")");
				
				System.out.println("Getting groups of UIDs: " + tempUIDs);
				Document groups = client.fql_query("SELECT gid FROM group WHERE gid IN " +
						"(SELECT gid FROM group_member WHERE uid IN " + tempUIDs + ")");
				NodeList ndeList = groups.getElementsByTagName("gid");
				
				if(ndeList.getLength() == 0)
				{
					System.out.println("This guy " + tempUIDs + " doesn't have any groups?");
					continue;
				}
				
				for(int a = 0; a < ndeList.getLength();)
				{
					StringBuilder tempGIDs = new StringBuilder("(");
					
					for(int i = 0; i < groupsPerCall && a < ndeList.getLength(); i++, a++)
					{
						String strGID = ndeList.item(a).getTextContent();

						if(lstGID.contains(strGID))
						{
							i--;
							continue;
						}
						
						if(i != 0) tempGIDs.append(", ");
						
						lstGID.add(strGID);							
						tempGIDs.append(strGID);
					}
					
					if(tempGIDs.length() < 2)
					{
						System.err.println("We already checked the all these groups");
						Thread.sleep(5000);
						continue;
					}
					
					tempGIDs.append(")");
					
					System.out.println("UID: " + tempUIDs + "; GIDs: " + tempGIDs);
					
					Document tempDoc = client.fql_query("SELECT uid FROM user " 
							+ "WHERE uid IN (SELECT uid, gid, positions FROM group_member WHERE gid IN "
							+ tempGIDs + ") "
							+ "AND \"MIT\" IN affiliations.name ");
//							+ "AND NOT (\"Alumnus/Alumna\" IN affiliations.status)"
//							+ "AND NOT (\"Faculty\" IN affiliations.status)"
//							+ "AND NOT (\"Staff\" IN affiliations.status)");
					
					System.out.println("Queure worked");
					
					NodeList uidList = tempDoc.getElementsByTagName("uid");
					
					System.out.println("Parsing worked");
					for(int b = 0; b < uidList.getLength(); b++)
					{
						String tempUID = uidList.item(b).getTextContent();
						System.out.println("UID: " + tempUID);
						if(!lstUID.contains(tempUID))
							lstUID.add(tempUID);
					}
					
					System.out.println("number of UID: " + lstUID.size() + "; number of GID: " + lstGID.size());
				}
			}
			catch(IOException ioe)
			{
				keepGoing++;
				
				for(int i = 0; i < groupsPerCall; i++) lstGID.remove(lstGID.size() - 1);
				name -= namesPerCall;

				System.out.println("KeepGoing: " + keepGoing);
				System.err.println("IO Excpetion again...");
			}
			catch(FacebookException fe)
			{
				keepGoing += 25;

				Thread.sleep(10000); // in ms
				
				client =
					new FacebookRestClient(props.getProperty("api_key"), props.getProperty("secret"));

				if (null != desktop && !"0".equals(desktop))
					client.setIsDesktop(true);

				auth = client.auth_createToken();
				browserLauncher.openURLinBrowser(props.getProperty("login_url") + "&api_key=" +
						props.getProperty("api_key") + "&auth_token=" + auth);

				System.out.println("Logging in...");
				try{client.auth_getSession(auth);}
				catch(Exception e){ keepGoing += 1000000; }

				System.out.println("KeepGoing: " + keepGoing);
				System.err.println("Error: " + fe.getMessage());
			}
			catch(Exception e)
			{
				keepGoing += 10000;
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		try
		{
			// Create file 
			FileWriter fstream = new FileWriter("C://post-fb-names.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			System.out.println("Writing names to file...");
			
			for(String uid: lstUID)
			{
				out.write(uid);
				out.newLine();
			}
			
			System.out.println("Done writing names to file...");
			
			//Close the output stream
			out.close();
			
			
			System.out.println("Writing groups to file...");
			fstream = new FileWriter("C://post-fb-groups.txt");
			out = new BufferedWriter(fstream);
			
			for(String gid: lstGID)
			{
				out.write(gid);
				out.newLine();
			}

			System.out.println("Done writing groups to file...");
			
			out.close();
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}//*/
	}
}
