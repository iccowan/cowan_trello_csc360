package trello;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * XMLSerializer Class
 * 
 * Pass an ArrayList of all the instances of an object in order to properly use
 * this serializer. Then you can use the deserializer to get an ArrayList of all
 * of the instances that have been serialized.
 *
 */
public class XMLSerializer
{

	/**
	 * @param <T>    - Type of the object to serialize
	 * @param object - Object to serialize
	 * @param String - Name of the class to serialize, for the filename
	 */
	public static <T> void serializeToXML(ArrayList<T> objectList, String className)
	{

		XMLEncoder encoder = null;

		try
		{
			String serializedFileName = className + ".xml";
			encoder = new XMLEncoder(
					new BufferedOutputStream(new FileOutputStream("serializedObjects/" + serializedFileName)));

		}
		catch (FileNotFoundException fileNotFound)
		{
			System.out.println("ERROR: While serializing " + className + "to XML");
		}

		encoder.writeObject(objectList);
		encoder.close();
	}

	/**
	 * @param <T>       - Type of the object to deserialize
	 * @param className - Class to deserialize
	 * @return ArrayList<T> - The deserialized object that's created
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> deserializeFromXML(String className)
	{
		XMLDecoder decoder = null;

		try
		{
			String serializedFileName = className + ".xml";
			decoder = new XMLDecoder(
					new BufferedInputStream(new FileInputStream("serializedObjects/" + serializedFileName)));
		}
		catch (FileNotFoundException e)
		{
			System.out.println("ERROR: File for " + className + " not found");
		}

		ArrayList<T> objectList = (ArrayList<T>) decoder.readObject();

		return objectList;
	}

}
