/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package praktikum1;

import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
/**
 *
 * @author NiklasPieper
 */
public class BasicSax5 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String filename;
        System.out.println("BasicSax5: Welcher XML-File soll geparst werden? (Eingabe mit Endung .xml bitte!)");
        filename= "Artikel.xml";
        MyContentHandler handler = new MyContentHandler();
        MyErrorHandler ehandler = new MyErrorHandler(); 
        System.out.println("Versuch: XML-File = "+filename+" zu oeffnen");
        parseXmlFile(filename, handler, ehandler, true);
    }
    
    public static void parseXmlFile(String filename, MyContentHandler handler, MyErrorHandler ehandler, boolean val) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(val);
            SAXParser saxpars1=factory.newSAXParser();
            XMLReader read1=saxpars1.getXMLReader();
            read1.setContentHandler(handler);
            read1.setErrorHandler(ehandler);
            boolean w1=saxpars1.isValidating();
            if(w1) System.out.println("---> Der Parser validiert.");
            String h1= new File(filename).toURL().toString();
            System.out.println("URI = "+h1);
            read1.parse(new File(filename).toURL().toString());
        }
        catch (SAXParseException ep) {
            // A parsing error occurred; the xml input is not valid
            System.out.println("SAx-Parser-Ausnahme in "+filename+" :\n"+ep);
            System.out.println("Parser meldet FEHLER : "+ep.toString());
            System.out.println("an der Entity        : "+ep.getPublicId());
            System.out.println("Zeile,Spalte         : "+ep.getLineNumber()+","+ep.getColumnNumber());
        }  
        catch (SAXException e) {
            // A parsing error occurred; the xml input is not valid
            System.out.println("Da ist eine XML-Invaliditaet in "+filename+" :\n"+e);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("Ein Parser-Konfigurationsproblem.");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("XML-File = "+filename+" konnte nicht geoeffnet werden");          
        }
    }
}

