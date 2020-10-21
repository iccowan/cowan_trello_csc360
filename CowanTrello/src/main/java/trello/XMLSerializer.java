package trello;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;

/**
 * XMLSerializer Class
 *
 */
public class XMLSerializer
{

	/**
	 * @param <T>    - Type of the object to serialize
	 * @param object - Object to serialize
	 * @return String - String of the serialized filename
	 */
	public static <T> String serializeToXML(T object)
	{

		XMLEncoder encoder = null;
		String serializedFileName = "";

		try
		{
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String timeStamp = String.valueOf(timestamp.getTime());
			
			serializedFileName = object.toString().substring(0, 19) + "_" + timeStamp + ".xml";
			encoder = new XMLEncoder(
					new BufferedOutputStream(new FileOutputStream("serializedObjects/" + serializedFileName)));

		}
		catch (FileNotFoundException fileNotFound)
		{
			System.out.println("ERROR: While serializing " + object.toString() + "to XML");
		}

		encoder.writeObject(object);
		encoder.close();
		
		return serializedFileName;
	}

	/**
	 * @param <T> - Type of the object to deserialize
	 * @param fileName - File that contains the object to deserialize
	 * @return T - The deserialized object that's created
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserializeFromXML(String fileName)
	{
		XMLDecoder decoder = null;

		try
		{
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("serializedObjects/" + fileName)));
		}
		catch (FileNotFoundException e)
		{
			System.out.println("ERROR: File " + fileName + " not found");
		}
		
		T object = (T) decoder.readObject();
		
		return object;
	}

}
