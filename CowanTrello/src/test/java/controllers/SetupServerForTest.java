package controllers;

import java.util.ArrayList;

import models.trello.User;

public class SetupServerForTest
{
	public static ArrayList<User> start()
	{
		// Create some users
		ArrayList<User> users = new ArrayList<User>();
		for (int i = 0; i < 30; i++)
		{
			User newUser = new User("user" + i, "password" + i);
			users.add(newUser);
		}
		User.serializeToXML(users);
		
		// Now, start the server
		try
		{
			models.trelloServer.TrelloServer.startServer("COWAN_TRELLO", 1099);
		} catch (Exception e)
		{
			// Server already running... continue
		}
		
		// Return the users that we created
		return users;
	}
}
