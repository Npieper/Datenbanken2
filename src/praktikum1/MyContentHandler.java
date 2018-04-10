/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package praktikum1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

 

public class MyContentHandler implements ContentHandler {
    
    Connection con;
    String insertAnf=new String("INSERT INTO ");
    String tabelle=new String();
    String spaltseq=new String();
    String values=new String(" VALUES");
    String wertseq=new String();
    int za=0;       /* Zeile aktiv <=> za=1               */
    int m=0;        /* lfd. Spaltenr.i.d. Zeile           */
    int cm=0;       /* DTYP(Spalte) ist SQL-char-aehnlich */
    String aktwert; /* Wert des aktuellen XML-Elements    */
    FileWriter pta; /* Zeichenorientierte Ausgabedatei    */
    PrintWriter pd1;/* Methodeninventar fuer Ausgabedatei */
    String dsn;     /* Name der SQL-Ausgabedatei          */ 
    List<String> selects = new ArrayList<>();
    
    Statement insertStatement;
 
    public void insertDB(String insert) throws SQLException {
        con = DB.connect();
        insertStatement = con.createStatement();
       
        BufferedReader in = null; 
        try { 
            in = new BufferedReader(new FileReader(dsn)); 
            String zeile = null; 
            while ((zeile = in.readLine()) != null) { 
                System.out.println("Gelesene Zeile: " + zeile); 
                selects.add(zeile);
            } 
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
        
        for(String s :selects) {
            String test = null;
            for(int i = 0;i < s.length();i++) {
                if(i == s.length() - 1) {
                    
                    test = s.substring(0, s.length()-1);
                }
            }
            System.out.println("Test:"+test);
            insertStatement.executeUpdate(test);
        }

    }
    public void startDocument() {
        System.out.println("Anfang des Parsens: ");
    }
    
    public void endDocument() {
        pd1.close();
        System.out.println("Ende des Parsens: "+dsn+" geschlossen.");
        System.out.println(dsn);
        try {
            insertDB(dsn);
        } catch (SQLException ex) {
            Logger.getLogger(MyContentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public void startElement(String uri,String localName, String qName,
                         Attributes attributes) throws SAXException { 
        int i;
        String gVl=null;
        String gTy=null;
        String gNam=null;
        String gVl4;
        AttributesImpl a1=new AttributesImpl(attributes);
        int l1=a1.getLength();
  
        // System.out.println("Element: "+qName+" Attributanzahl: "+l1);
        for(i=0; i<l1; i++) {
            gVl=a1.getValue(i);
            gTy=a1.getType(i);
            gNam=a1.getQName(i);
             // System.out.println("++"+i+". Attribut: "+gNam+" ("+gTy+") : "+gVl);
        }
        
        /* Wenn qName = Zeile ist */
        if (qName.compareTo("zeile")==0) { 
            za=1;
            spaltseq=" (";
            wertseq=" (";
            qName="";
            return ;
        }
    
         /* Wenn wir in einer Zeile sind */
        if (za==1)  /* za==1 <=> Zeile ist aktiv    */ { 
            /* Verarbeitung von char-aehnlichen SQL-Datentypen */
            if (a1.getQName(0).compareTo("DT")==0) { 
               
                if (gVl.length()>=4) { 
                    gVl4=gVl.substring(0,4);
                    if (gVl4.compareTo("char")==0) {
                        cm=1;
                    } else if(gVl.compareTo("date") == 0) {
                       cm = 1; 
                    }
                } 
             } 
        
            if (m==0) /* 1. Spalte in der Tabellenzeile */ { 
                spaltseq=spaltseq+qName;
             }
            else spaltseq=spaltseq+","+qName;
        } 
    } 

  public void characters(char[] ch, int start, int length) throws SAXException
  {String h=null;
   h=new String(ch,start,length);
   // System.out.println("-> Start: "+start+" Laenge: "+length+" : "+h+" .");
   aktwert=h;
  } 

  public void skippedEntity(String name) throws SAXException
  { //System.out.println("-S-> Skipped Entity: "+name+" .");
  } 

  public void processingInstruction(String target, String data) throws SAXException
  { //System.out.println("-P-> Process_Instr: "+target+" "+data+" .");
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
  { //System.out.println("-W-> Whitespace: Pos: "+start+" Laenge: "+length+" .");
  }

  public void endElement(String uri, String localName, String qName) throws SAXException
  { //System.out.println("-E-> Ende des Elements: "+qName+" .");
    /* Tabellenname setzen             */
    if (qName.compareTo("tabname")==0)
     { tabelle=aktwert;
       try
       { dsn="LOAD_"+tabelle+".sql";
         pta=new FileWriter(dsn);
         pd1=new PrintWriter(pta);
       }
       catch (IOException ex1)
       { System.out.println("Fehler beim Oeffnen von "+dsn+" : "+ex1.toString());
       }
     }
    /* Zeilenendeverarbeitung          */
    if (qName.compareTo("zeile")==0) {
       String insert;
       spaltseq=spaltseq+")";
       wertseq=wertseq+")";
       za=0;
       m=0;
       insert=insertAnf+tabelle+spaltseq+values+wertseq+";";
       System.out.println("---> "+insert);
       pd1.println(insert);
     }
    /* Verarbeitung der Elemente in einer Zeile */
    if (za==1)     /* za==1 <=> Zeile ist aktiv    */ 
     { String hoch=new String(); 
       if (cm==1)  /* DTYP(Spalte) ist SQL-char-aehnlich */
       { hoch="'";
         cm=0;
       }
       else hoch="";
       if (m==0)
       { m=1;
         wertseq=wertseq+hoch+aktwert+hoch;
       }
       else
       { m=m+1;
         wertseq=wertseq+","+hoch+aktwert+hoch;
       }
     }
  }

  public void endPrefixMapping(String prefix) throws SAXException
  { //System.out.println("-Pr-> Praefix: "+prefix);
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException
  { //System.out.println("-PrS-> Praefix: "+prefix);
  }
 
  public void setDocumentLocator(Locator locator)
  { //System.out.println("-L-> Locator: "+locator.toString());
  }
 }
